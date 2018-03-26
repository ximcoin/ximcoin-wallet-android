package com.ximcoin.ximwallet.presenter.inflation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Operation;
import org.stellar.sdk.SetOptionsOperation;
import org.stellar.sdk.Transaction;

import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.api.HorizonApi;
import com.ximcoin.ximwallet.model.fees.Fees;
import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.model.repository.AccountRepository;
import com.ximcoin.ximwallet.model.util.AccountUtil;
import com.ximcoin.ximwallet.model.util.FeesUtil;
import com.ximcoin.ximwallet.model.util.SeedEncryptionUtil;
import com.ximcoin.ximwallet.model.util.TransactionUtil;
import com.ximcoin.ximwallet.presenter.common.BasePresenter;
import timber.log.Timber;

public class InflationPresenter extends BasePresenter<InflationContract.InflationView>
        implements InflationContract.InflationPresenter {
    @NonNull
    private final HorizonApi horizonApi;

    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @Nullable
    private Transaction pendingTransaction;

    private String accountId;
    private boolean isBuildingTransaction = false;
    private boolean isPostingTransaction = false;

    InflationPresenter(@NonNull InflationContract.InflationView view,
                       @NonNull HorizonApi horizonApi,
                       @NonNull AccountRepository accountRepository,
                       @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.horizonApi = horizonApi;
        this.schedulerProvider = schedulerProvider;
        this.accountRepository = accountRepository;
    }

    @Override
    public void start(@Nullable Bundle bundle) {
        super.start(bundle);
        accountId = view.getAccountId();
        loadAccountInflationDestination();
    }

    private void loadAccountInflationDestination() {
        accountRepository.getAccountById(accountId, false)
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> view.showLoading(false))
                .subscribe(account ->
                        view.showCurrentInflationAddress(account.getInflation_destination()));
    }

    @Override
    public void onUserSetInflationDestination(@Nullable String destination) {
        if (isBuildingTransaction) {
            return;
        }

        isBuildingTransaction = true;

        InflationError error = null;

        if (!AccountUtil.publicKeyOfProperLength(destination)) {
            error = InflationError.ADDRESS_LENGTH;
        } else if (!AccountUtil.publicKeyOfProperPrefix(destination)) {
            error = InflationError.ADDRESS_PREFIX;
        } else if (!AccountUtil.publicKeyCanBeDecoded(destination)) {
            error = InflationError.ADDRESS_FORMAT;
        }

        if (error != null) {
            view.showInflationError(error);
            isBuildingTransaction = false;
            return;
        }

        horizonApi.getAccount(accountId)
                .flatMapCompletable(account ->
                        horizonApi.getFees()
                                .toObservable()
                                .doOnNext(feesWrapper -> buildTransactionConfirmation(account,
                                        feesWrapper.getFees(), destination))
                                .ignoreElements())
                .compose(schedulerProvider.completableScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showBlockedLoading(true);
                })
                .doAfterTerminate(() -> isBuildingTransaction = false)
                .subscribe(() -> view.hideBlockedLoading(true, true),
                        throwable -> {
                            Timber.e(throwable, "Failed to build inflation transaction");
                            view.hideBlockedLoading(true, false);
                        });
    }

    private void buildTransactionConfirmation(@NonNull Account account,
                                              @NonNull Fees fees,
                                              @NonNull String inflationDestination) {
        String currentDest = account.getInflation_destination();
        if (currentDest != null && currentDest.equals(inflationDestination)) {
            view.showInflationError(InflationError.ALREADY_SET);
            throw new IllegalArgumentException("Inflation already set to this address");
        }

        double lumensBalance = account.getLumens().getBalance();
        double minimumBalance = FeesUtil.getMinimumAccountBalance(fees, account);
        double feeAmount = FeesUtil.getTransactionFee(fees, 1);

        if (lumensBalance - feeAmount < minimumBalance) {
            view.showInsufficientFundsError(minimumBalance);
            return;
        }

        pendingTransaction = new Transaction.Builder(account)
                .addOperation(getInflationOperation(inflationDestination, account.getAccount_id()))
                .build();

        view.showTransactionConfirmation(new InflationOperationSummary(inflationDestination,
                feeAmount));
    }

    private static Operation getInflationOperation(@NonNull String inflationDestination,
                                                   @NonNull String accountId) {
        return new SetOptionsOperation.Builder()
                .setInflationDestination(KeyPair.fromAccountId(inflationDestination))
                .setSourceAccount(KeyPair.fromAccountId(accountId))
                .build();
    }

    @Override
    public void onUserConfirmedTransaction(@Nullable String password) {
        if (isPostingTransaction) {
            return;
        }

        isPostingTransaction = true;

        if (pendingTransaction == null) {
            Timber.e("Pending transaction was null");
            isPostingTransaction = false;
            return;
        }

        if (!SeedEncryptionUtil.checkPasswordLength(password)) {
            view.showInflationError(InflationError.PASSWORD_LENGTH);
            isPostingTransaction = false;
            return;
        }

        final String sourceAccountId = pendingTransaction.getSourceAccount().getAccountId();
        accountRepository
                .getEncryptedSeed(sourceAccountId)
                .map(accountPrivateKey -> {
                    KeyPair signer = KeyPair.fromSecretSeed(SeedEncryptionUtil
                            .decryptSeed(accountPrivateKey.getEncryptedSeedPackage(), password));
                    pendingTransaction.sign(signer);
                    return pendingTransaction;
                })
                .flatMapCompletable(transaction ->
                        horizonApi.postTransaction(TransactionUtil.getEnvelopeXDRBase64(transaction)))
                .andThen(accountRepository.getAccountById(sourceAccountId, true))
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showBlockedLoading(false);
                })
                .doAfterTerminate(() -> {
                    pendingTransaction = null;
                    isPostingTransaction = false;
                })
                .subscribe(account ->
                                view.hideBlockedLoading(false, true),
                        throwable -> {
                            Timber.e(throwable, "Failed to post inflation transaction");
                            view.hideBlockedLoading(false, false);
                        });
    }
}
