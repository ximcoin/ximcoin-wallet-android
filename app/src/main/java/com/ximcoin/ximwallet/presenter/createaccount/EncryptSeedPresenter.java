package com.ximcoin.ximwallet.presenter.createaccount;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.stellar.sdk.KeyPair;

import io.reactivex.Completable;
import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.persistence.HorizonDB;
import com.ximcoin.ximwallet.model.persistence.account.AccountPrivateKey;
import com.ximcoin.ximwallet.model.persistence.account.AccountPrivateKeyDao;
import com.ximcoin.ximwallet.model.persistence.account.EncryptedSeedPackage;
import com.ximcoin.ximwallet.model.util.SeedEncryptionUtil;
import com.ximcoin.ximwallet.presenter.common.BasePresenter;
import com.ximcoin.ximwallet.view.util.TextUtils;
import com.ximcoin.ximwallet.view.util.ViewUtils;
import timber.log.Timber;

public class EncryptSeedPresenter extends BasePresenter<EncryptSeedContract.EncryptSeedView>
        implements EncryptSeedContract.EncryptSeedPresenter {
    @NonNull
    private final AccountPrivateKeyDao accountPrivateKeyDao;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    private boolean isInEncryptionProcess = false;

    EncryptSeedPresenter(@NonNull EncryptSeedContract.EncryptSeedView view,
                         @NonNull HorizonDB horizonDB,
                         @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.accountPrivateKeyDao = horizonDB.accountPrivateKeyDao();
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void start(@Nullable Bundle bundle) {
        super.start(bundle);
        checkFinishEnabled();
    }

    @Override
    public void onUserFinished(@Nullable String password,
                               @Nullable String passwordValidation,
                               @Nullable String seed) {
        if (TextUtils.isEmpty(seed)) {
            // Something went very wrong.
            Timber.e("Seed was empty");
            view.showSomethingWrongError();
        }

        if (isInEncryptionProcess) {
            return;
        }

        if (!checkFieldErrors(password, passwordValidation)) {
            encryptSeed(seed, password);
        } else {
            view.setFinishEnabled(true);
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
        hideAllFieldErrors();
        view.setFinishEnabled(true);
    }

    private void hideAllFieldErrors() {
        ViewUtils.whenNonNull(view, v -> {
            v.hidePasswordLengthError();
            v.hidePasswordMismatchError();
        });
    }

    /**
     * Checks for field errors and displays the most heinous one.
     *
     * @return {@code True} if one was found; otherwise the form is valid for submission.
     */
    private boolean checkFieldErrors(@Nullable String password,
                                     @Nullable String passwordValidation) {
        hideAllFieldErrors();

        if (!SeedEncryptionUtil.checkPasswordLength(password)) {
            view.showPasswordLengthError();
            return true;
        } else if (!checkPasswordsMatch(password, passwordValidation)) {
            view.showPasswordMismatchError();
            return true;
        }

        // Primary password field is of proper length. Secondary password field either matches or
        // is not visible due to primary password field content visibility. Form is valid!
        return false;
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

    private void encryptSeed(@NonNull String seed,
                             @NonNull String password) {
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
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doOnTerminate(() -> view.showLoading(false))
                .subscribe(view::finish,
                        throwable -> {
                            isInEncryptionProcess = false;
                            Timber.e(throwable, "Failed to encrypt/insert seed");
                            view.showSomethingWrongError();
                        });
    }
}
