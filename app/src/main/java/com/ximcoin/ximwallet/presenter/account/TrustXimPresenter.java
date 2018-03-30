package com.ximcoin.ximwallet.presenter.account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.api.HorizonApi;
import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.model.repository.AccountRepository;
import com.ximcoin.ximwallet.model.util.AssetUtil;
import com.ximcoin.ximwallet.model.util.SeedEncryptionUtil;
import com.ximcoin.ximwallet.model.util.TransactionUtil;
import com.ximcoin.ximwallet.presenter.common.BasePresenter;

import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Operation;
import org.stellar.sdk.Transaction;

import timber.log.Timber;

public class TrustXimPresenter extends BasePresenter<TrustXimContract.TrustXimView>
        implements TrustXimContract.TrustXimPresenter {
    @NonNull
    private final HorizonApi horizonApi;

    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    private boolean isPostingTransaction = false;

    protected TrustXimPresenter(@NonNull TrustXimContract.TrustXimView view,
                                @NonNull HorizonApi horizonApi,
                                @NonNull AccountRepository accountRepository,
                                @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.horizonApi = horizonApi;
        this.accountRepository = accountRepository;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void trustXim(@Nullable String password) {
        if (isPostingTransaction) {
            return;
        }

        isPostingTransaction = true;

        if (!SeedEncryptionUtil.checkPasswordLength(password)) {
            view.showPasswordInvalidError();
            isPostingTransaction = false;
            return;
        }

        Account account = view.getCurrentAccount();
        if (account == null) {
            view.showTransactionBuildFailure();
            Timber.e("Account was null, shouldn't ever happen!");
            isPostingTransaction = false;
            return;
        }

        accountRepository
                .getEncryptedSeed(account.getAccount_id())
                .compose(schedulerProvider.singleScheduler())
                .flatMapCompletable(accountPrivateKey -> {
                    KeyPair signer = KeyPair.fromSecretSeed(SeedEncryptionUtil
                            .decryptSeed(accountPrivateKey.getEncryptedSeedPackage(), password));
                    Transaction trustTransaction =
                            new Transaction.Builder(account)
                                    .addOperation(getTrustOperation(account))
                                    .build();
                    trustTransaction.sign(signer);

                    return horizonApi.postTransaction(
                            TransactionUtil.getEnvelopeXDRBase64(trustTransaction))
                            .compose(schedulerProvider.completableScheduler());
                })
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showTrustTransactionInProgress();
                })
                .doAfterTerminate(() -> isPostingTransaction = false)
                .subscribe(() -> view.hideTrustTransactionProgress(true),
                        throwable -> {
                            view.hideTrustTransactionProgress(false);
                            Timber.e(throwable, "Failed to set XIM trust");
                        });
    }

    private Operation getTrustOperation(@NonNull Account account) {
        return new ChangeTrustOperation.Builder(AssetUtil.XIM_ASSET, AssetUtil.XIM_ASSET_LIMIT)
                .setSourceAccount(KeyPair.fromAccountId(account.getAccount_id()))
                .build();
    }
}
