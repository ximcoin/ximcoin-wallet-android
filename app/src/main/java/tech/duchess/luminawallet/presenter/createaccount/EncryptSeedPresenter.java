package tech.duchess.luminawallet.presenter.createaccount;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.stellar.sdk.KeyPair;

import javax.inject.Inject;

import io.reactivex.Completable;
import tech.duchess.luminawallet.model.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.persistence.HorizonDB;
import tech.duchess.luminawallet.model.persistence.account.AccountPrivateKey;
import tech.duchess.luminawallet.model.persistence.account.AccountPrivateKeyDao;
import tech.duchess.luminawallet.model.persistence.account.EncryptedSeedPackage;
import tech.duchess.luminawallet.model.util.SeedEncryptionUtil;
import tech.duchess.luminawallet.view.createaccount.IEncryptSeedView;
import tech.duchess.luminawallet.view.util.TextUtils;
import tech.duchess.luminawallet.view.util.ViewBindingUtils;
import timber.log.Timber;

public class EncryptSeedPresenter {
    // Make sure to update the respective xml layout and copy when changing the length requirements.
    private static final int MAX_PASSWORD_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 8;

    @NonNull
    private final AccountPrivateKeyDao accountPrivateKeyDao;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final LifecycleProvider<FragmentEvent> lifecycleProvider;

    @Nullable
    private IEncryptSeedView view;

    private boolean isInEncryptionProcess = false;

    @Inject
    public EncryptSeedPresenter(@NonNull HorizonDB horizonDB,
                                @NonNull SchedulerProvider schedulerProvider,
                                @NonNull LifecycleProvider<FragmentEvent> lifecycleProvider) {
        this.accountPrivateKeyDao = horizonDB.accountPrivateKeyDao();
        this.schedulerProvider = schedulerProvider;
        this.lifecycleProvider = lifecycleProvider;
    }

    public void attachView(@NonNull IEncryptSeedView view) {
        this.view = view;
        checkFinishEnabled();
    }

    public void detachView() {
        view = null;
    }

    public void onUserFinished() {
        if (isInEncryptionProcess) {
            return;
        }

        if (!checkFieldErrors()) {
            encryptSeed();
        } else {
            ViewBindingUtils.whenNonNull(view, v -> v.setFinishEnabled(false));
        }
    }

    public void onPasswordContentsChanged() {
        checkFinishEnabled();
    }

    /**
     * Invoked whenever the user has switched focus on the password fields or toggled the password
     * visibility on the primary password field.
     * <p>
     * This always leans towards hiding errors and enabling the finish button. The finish button
     * will be responsible for displaying the most egregious error during submission.
     */
    private void checkFinishEnabled() {
        if (view == null) {
            return;
        }

        hideAllFieldErrors();
        view.setFinishEnabled(true);
    }

    private void hideAllFieldErrors() {
        ViewBindingUtils.whenNonNull(view, v -> {
            v.hidePasswordLengthError();
            v.hidePasswordMismatchError();
        });
    }

    /**
     * Checks for field errors and displays the most heinous one.
     *
     * @return {@code True} if one was found; otherwise the form is valid for submission.
     */
    private boolean checkFieldErrors() {
        if (view == null) {
            return true;
        }

        hideAllFieldErrors();

        String primaryPassword = view.getPrimaryFieldContents();

        if (!checkPasswordLength(primaryPassword)) {
            view.showPasswordLengthError();
            return true;
        } else if (!checkPasswordsMatch(primaryPassword, view.getSecondaryFieldContents())) {
            view.showPasswordMismatchError();
            return true;
        }

        // Primary password field is of proper length. Secondary password field either matches or
        // is not visible due to primary password field content visibility. Form is valid!
        return false;
    }

    /**
     * Checks the password length.
     *
     * @param password The password to check.
     * @return {@code True} if the password is valid length.
     */
    private boolean checkPasswordLength(@Nullable String password) {
        return password != null
                && password.length() >= MIN_PASSWORD_LENGTH
                && password.length() <= MAX_PASSWORD_LENGTH;
    }

    /**
     * Checks if the passwords match.
     *
     * @param primaryPassword      The primary password field contents.
     * @param confirmationPassword The confirmation password field contents.
     * @return {@code True} if the fields are not empty and match; otherwise {@code false}.
     */
    private boolean checkPasswordsMatch(@Nullable String primaryPassword,
                                        @Nullable String confirmationPassword) {
        if (TextUtils.isEmpty(primaryPassword) || TextUtils.isEmpty(confirmationPassword)) {
            return false;
        }

        return primaryPassword.equals(confirmationPassword);
    }

    private void encryptSeed() {
        if (view == null) {
            return;
        }

        final String seed = view.getSeed();
        final String password = view.getPrimaryFieldContents();

        if (TextUtils.isEmpty(seed) || TextUtils.isEmpty(password)) {
            view.showSomethingWrongError();
            return;
        }

        isInEncryptionProcess = true;
        Completable.fromAction(() -> {
            KeyPair keyPair = KeyPair.fromSecretSeed(seed);
            String accountId = keyPair.getAccountId();
            EncryptedSeedPackage encryptedSeedPackage =
                    SeedEncryptionUtil.encryptSeed(seed, password);
            AccountPrivateKey accountPrivateKey = new AccountPrivateKey();
            accountPrivateKey.setAccountId(accountId);
            accountPrivateKey.setEncryptedSeedPackage(encryptedSeedPackage);
            accountPrivateKeyDao.insert(accountPrivateKey);
        })
                .compose(schedulerProvider.completableScheduler())
                .compose(lifecycleProvider.bindUntilEvent(FragmentEvent.PAUSE))
                .doOnSubscribe(disposable -> view.showLoading(true))
                .doOnTerminate(() -> view.showLoading(false))
                .subscribe(() -> view.finish(),
                        throwable -> {
                            isInEncryptionProcess = false;
                            Timber.e(throwable, "Failed to encrypt/insert seed");
                            view.showSomethingWrongError();
                        });
    }
}
