package com.ximcoin.ximwallet.presenter.createaccount;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.api.HorizonApi;
import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.model.persistence.account.DisconnectedAccount;
import com.ximcoin.ximwallet.model.repository.FeesRepository;
import com.ximcoin.ximwallet.model.util.AccountUtil;
import com.ximcoin.ximwallet.model.util.AssetUtil;
import com.ximcoin.ximwallet.model.util.FeesUtil;
import com.ximcoin.ximwallet.model.util.TransactionUtil;
import com.ximcoin.ximwallet.presenter.common.BasePresenter;

import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Operation;
import org.stellar.sdk.Transaction;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.HttpException;
import timber.log.Timber;

public class ImportSeedPresenter extends BasePresenter<ImportSeedContract.ImportSeedView>
        implements ImportSeedContract.ImportSeedPresenter {
    @NonNull
    private final HorizonApi horizonApi;

    @NonNull
    private final FeesRepository feesRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    ImportSeedPresenter(@NonNull ImportSeedContract.ImportSeedView view,
                        @NonNull HorizonApi horizonApi,
                        @NonNull FeesRepository feesRepository,
                        @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.horizonApi = horizonApi;
        this.feesRepository = feesRepository;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void onUserImportSeed(@Nullable String seed) {
        if (!AccountUtil.secretSeedOfProperLength(seed)) {
            view.showError(ImportError.SEED_LENGTH_ERROR);
        } else if (!AccountUtil.secretSeedOfProperPrefix(seed)) {
            view.showError(ImportError.SEED_PREFIX_ERROR);
        } else if (!AccountUtil.secretSeedCanBeDecoded(seed)) {
            view.showError(ImportError.SEED_FORMAT_ERROR);
        } else {
            trustXimIfNeeded(seed);
        }
    }

    private void trustXimIfNeeded(@NonNull String seed) {
        String accountId = KeyPair.fromSecretSeed(seed).getAccountId();
        horizonApi.getAccount(accountId)
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof HttpException
                            && ((HttpException) throwable).code() == 404) {
                        return Single.just(new DisconnectedAccount(accountId));
                    }

                    return Single.error(throwable);
                })
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showXimSetupLoading(true);
                })
                .subscribe(account -> {
                    if (AccountUtil.trustsXim(account)) {
                        view.showXimSetupLoading(false);
                        view.onSeedValidated(seed);
                    } else {
                        trustXimIfPossible(seed, account);
                    }
                });
    }

    private void trustXimIfPossible(@NonNull String seed, @NonNull Account account) {
        feesRepository.getFees()
                .compose(schedulerProvider.singleScheduler())
                .flatMapCompletable(fees -> {
                    double curMinBalance = FeesUtil.getMinimumAccountBalance(fees, account);
                    double curLumens = account.getLumens().getBalance();
                    // If there are enough lumens greater than the current minimum balance to cover
                    // the new minimum balance and transaction fee, execute the trust transaction.
                    if (curLumens - curMinBalance - Double.parseDouble(fees.getBase_reserve())
                            - FeesUtil.getTransactionFee(fees, 1) >= 0) {
                        Transaction trustTransaction =
                                new Transaction.Builder(account)
                                        .addOperation(getTrustOperation(account))
                                        .build();
                        trustTransaction.sign(KeyPair.fromSecretSeed(seed));
                        return horizonApi.postTransaction(
                                TransactionUtil.getEnvelopeXDRBase64(trustTransaction))
                                .compose(schedulerProvider.completableScheduler());
                    } else {
                        // Not enough lumens. Not a big deal, as downstream views handle this.
                        return Completable.complete();
                    }
                })
                .doOnSubscribe(this::addDisposable)
                .doFinally(() -> {
                    // If we failed to trust XIM here, not the end of the world. Downstream views
                    // handle it. So move on regardless of success.
                    view.showXimSetupLoading(false);
                    view.onSeedValidated(seed);
                })
                .subscribe(() -> {},
                        throwable -> Timber.e(throwable, "Failed to trust XIM"));
    }

    private Operation getTrustOperation(@NonNull Account account) {
        return new ChangeTrustOperation.Builder(AssetUtil.XIM_ASSET, AssetUtil.XIM_ASSET_LIMIT)
                .setSourceAccount(KeyPair.fromAccountId(account.getAccount_id()))
                .build();
    }

    @Override
    public void onSeedFieldContentsChanged() {
        view.clearError();
    }
}
