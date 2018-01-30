package tech.duchess.luminawallet.presenter.account.send;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.util.concurrent.AtomicDouble;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.CreateAccountOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Operation;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.HttpException;
import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.api.HorizonApi;
import tech.duchess.luminawallet.model.fees.Fees;
import tech.duchess.luminawallet.model.persistence.HorizonDB;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.persistence.account.Balance;
import tech.duchess.luminawallet.model.persistence.account.DisconnectedAccount;
import tech.duchess.luminawallet.model.repository.AccountRepository;
import tech.duchess.luminawallet.model.util.AccountUtil;
import tech.duchess.luminawallet.model.util.AssetUtil;
import tech.duchess.luminawallet.model.util.FeesUtil;
import tech.duchess.luminawallet.model.util.SeedEncryptionUtil;
import tech.duchess.luminawallet.model.util.TransactionUtil;
import tech.duchess.luminawallet.presenter.common.BasePresenter;
import tech.duchess.luminawallet.view.util.TextUtils;
import tech.duchess.luminawallet.view.util.ViewUtils;
import timber.log.Timber;

public class SendPresenter extends BasePresenter<SendContract.SendView>
        implements SendContract.SendPresenter {
    private static final String ACCOUNT_KEY = "SendPresenter.ACCOUNT_KEY";

    @NonNull
    private final HorizonApi horizonApi;

    @NonNull
    private final HorizonDB horizonDB;

    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @Nullable
    private Account sourceAccount;

    @Nullable
    private Transaction pendingTransaction;

    private final Map<String, String> assetCodeToIssuerMap = new HashMap<>();

    SendPresenter(@NonNull SendContract.SendView view,
                  @NonNull HorizonApi horizonApi,
                  @NonNull HorizonDB horizonDB,
                  @NonNull AccountRepository accountRepository,
                  @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.horizonApi = horizonApi;
        this.horizonDB = horizonDB;
        this.accountRepository = accountRepository;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void onUserSendPayment(@Nullable String recipient,
                                  @Nullable String amount,
                                  @Nullable String currency,
                                  @Nullable String memo) {
        if (sourceAccount == null) {
            return;
        }

        SendError error = null;
        final AtomicDouble currencyBalance = new AtomicDouble();
        final AtomicDouble sendAmount = new AtomicDouble();
        // First check what we can without making network transactions.
        if (!AccountUtil.publicKeyOfProperLength(recipient)) {
            error = SendError.ADDRESS_BAD_LENGTH;
        } else if (!AccountUtil.publicKeyOfProperPrefix(recipient)) {
            error = SendError.ADDRESS_BAD_PREFIX;
        } else if (recipient.equals(sourceAccount.getAccount_id())) {
            error = SendError.DEST_SAME_AS_SOURCE;
        } else if (TextUtils.isEmpty(amount) || Double.parseDouble(amount) <= 0) {
            error = SendError.AMOUNT_GREATER_THAN_ZERO;
        } else if (TextUtils.isEmpty(currency) || !assetCodeToIssuerMap.containsKey(currency)) {
            Timber.e("Couldn't find currency: %s", currency);
            error = SendError.ADDRESS_UNSUPPORTED_CURRENCY;
        } else {
            String assetIssuer = assetCodeToIssuerMap.get(currency);
            currencyBalance.set(Observable.fromIterable(sourceAccount.getBalances())
                    .filter(balance ->
                            balance.getAsset_issuer().equals(assetIssuer)
                                    && balance.getAsset_code().equals(currency))
                    .blockingFirst()
                    .getBalance());
            sendAmount.set(Double.parseDouble(amount));

            if (currencyBalance.get() - sendAmount.get() < 0) {
                error = SendError.INSUFFICIENT_FUNDS;
            }
        }

        if (error != null) {
            view.showError(error);
            return;
        }

        horizonApi.getAccount(recipient)
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> view.showLoading(false))
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof HttpException
                            && ((HttpException) throwable).code() == 404) {
                        return Single.just(new DisconnectedAccount(recipient));
                    }

                    return Single.error(throwable);
                })
                .flatMapCompletable(account ->
                        horizonApi.getFees()
                                .toObservable()
                                .doOnNext(feesWrapper -> buildTransactionConfirmation(sourceAccount,
                                        account, currency, assetCodeToIssuerMap.get(currency),
                                        sendAmount.get(), feesWrapper.getFees(),
                                        currencyBalance.get(), memo))
                                .ignoreElements())
                .compose(schedulerProvider.completableScheduler())
                .subscribe(() -> {

                }, throwable -> {
                    Timber.e(throwable, "Failed to build transaction");
                    view.showError(SendError.TRANSACTION_BUILD_FAILED);
                });
    }

    private void buildTransactionConfirmation(@NonNull Account sourceAccount,
                                              @NonNull Account recipient,
                                              @NonNull String assetCode,
                                              @NonNull String assetIssuer,
                                              double sendAmount,
                                              @NonNull Fees fees,
                                              double assetBalance,
                                              @Nullable String memo) {
        boolean isNativeAsset = AssetUtil.LUMEN_ASSET_CODE.equals(assetCode);

        if (!recipient.isOnNetwork() && !isNativeAsset) {
            view.showError(SendError.ADDRESS_DOES_NOT_EXIST);
            return;
        } else if (!AccountUtil.trustsAsset(recipient, assetCode, assetIssuer)) {
            view.showError(SendError.ADDRESS_UNSUPPORTED_CURRENCY);
            return;
        }

        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.fees = FeesUtil.getTransactionFee(fees, 1);
        transactionSummary.minimumBalance = FeesUtil.getMinimumAccountBalance(fees, sourceAccount);
        transactionSummary.sendAmount = sendAmount;
        transactionSummary.assetCode = assetCode;
        transactionSummary.isCreatingAccount = !recipient.isOnNetwork();
        double newLumenBalance = sourceAccount.getLumens().getBalance() - transactionSummary.fees;

        if (isNativeAsset) {
            // Sending lumens
            newLumenBalance -= sendAmount;
            transactionSummary.remainingBalances.put(assetCode, newLumenBalance);
        } else {
            // Sending a different asset
            transactionSummary.remainingBalances.put(AssetUtil.LUMEN_ASSET_CODE, newLumenBalance);
            assetBalance -= sendAmount;
            transactionSummary.remainingBalances.put(assetCode, assetBalance);
        }

        transactionSummary.minimumBalanceViolated =
                newLumenBalance < transactionSummary.minimumBalance;

        if (!transactionSummary.minimumBalanceViolated) {
            Operation operation;
            if (transactionSummary.isCreatingAccount) {
                transactionSummary.createdAccountBalanceFulfilled =
                        sendAmount >= Double.parseDouble(fees.getBase_reserve());
                operation = getAccountCreationOperation(sourceAccount, recipient.getAccount_id(),
                        String.valueOf(sendAmount));
            } else {
                operation = getPaymentOperation(sourceAccount, recipient.getAccount_id(),
                        assetCode, assetIssuer, String.valueOf(sendAmount));
            }

            pendingTransaction = new Transaction.Builder(sourceAccount)
                    .addOperation(operation)
                    .addMemo(getMemo(memo))
                    .build();
        }

        view.showConfirmation(transactionSummary);
    }

    @Override
    public void onUserConfirmPayment(@Nullable String password) {
        if (pendingTransaction == null) {
            Timber.e("Pending transaction was null");
            view.showError(SendError.TRANSACTION_FAILED);
            view.clearForm();
            return;
        }

        if (!SeedEncryptionUtil.checkPasswordLength(password)) {
            view.showError(SendError.PASSWORD_INVALID);
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
                        horizonApi.postTransaction(
                                TransactionUtil.getEnvelopeXDRBase64(transaction)))
                .andThen(accountRepository.getAccountById(sourceAccountId, true))
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> {
                    view.showLoading(false);
                    pendingTransaction = null;
                })
                .subscribe(view::showTransactionSuccess,
                        throwable -> {
                            Timber.e(throwable, "Transaction failed");
                            view.showError(SendError.TRANSACTION_FAILED);
                        });
    }

    @Override
    public void onAccountUpdated(@Nullable Account account) {
        sourceAccount = account;
        view.clearForm();
        updateView();
    }

    private static Operation getPaymentOperation(@NonNull Account sourceAccount,
                                                 @NonNull String recipient,
                                                 @NonNull String currency,
                                                 @NonNull String currencyIssuer,
                                                 @NonNull String amount) {
        Asset asset;
        if (AssetUtil.LUMEN_ASSET_CODE.equals(currency)) {
            asset = new AssetTypeNative();
        } else {
            asset = Asset.createNonNativeAsset(currency, KeyPair.fromAccountId(currencyIssuer));
        }

        return new PaymentOperation.Builder(
                KeyPair.fromAccountId(recipient),
                asset,
                amount)
                .setSourceAccount(KeyPair.fromAccountId(sourceAccount.getAccount_id()))
                .build();
    }

    private static Operation getAccountCreationOperation(@NonNull Account sourceAccount,
                                                         @NonNull String recipient,
                                                         @NonNull String amount) {
        return new CreateAccountOperation.Builder(
                KeyPair.fromAccountId(recipient), amount)
                .setSourceAccount(KeyPair.fromAccountId(sourceAccount.getAccount_id()))
                .build();
    }

    private Memo getMemo(@Nullable String memo) {
        if (TextUtils.isEmpty(memo)) {
            return Memo.none();
        } else {
            return Memo.text(memo);
        }
    }

    @Override
    public void saveState(@Nullable Bundle bundle) {
        super.saveState(bundle);
        ViewUtils.whenNonNull(bundle, b -> b.putParcelable(ACCOUNT_KEY, sourceAccount));
    }

    @Override
    public void start(@Nullable Bundle bundle) {
        super.start(bundle);
        ViewUtils.whenNonNull(bundle, b -> sourceAccount = b.getParcelable(ACCOUNT_KEY));
        updateView();
    }

    private void updateView() {
        if (sourceAccount == null) {
            view.showNoAccount();
        }

        populateAssets();
    }

    private void populateAssets() {
        assetCodeToIssuerMap.clear();

        if (sourceAccount != null) {
            for (Balance balance : sourceAccount.getBalances()) {
                assetCodeToIssuerMap.put(balance.getAsset_code(), balance.getAsset_issuer());
            }
        }

        view.setAvailableCurrencies(new ArrayList<>(assetCodeToIssuerMap.keySet()));
    }

    private class TransactionSummary implements SendContract.SendPresenter.TransactionSummary {
        double minimumBalance;
        double fees;
        double sendAmount;
        String assetCode;
        final LinkedHashMap<String, Double> remainingBalances = new LinkedHashMap<>();
        boolean minimumBalanceViolated;
        boolean isCreatingAccount;
        boolean createdAccountBalanceFulfilled;

        @Override
        public double getMinimumBalance() {
            return minimumBalance;
        }

        @Override
        public double getFees() {
            return fees;
        }

        @Override
        public double getSendAmount() {
            return sendAmount;
        }

        @Override
        public String getAssetCode() {
            return assetCode;
        }

        @Override
        public LinkedHashMap<String, Double> getRemainingBalances() {
            return remainingBalances;
        }

        @Override
        public boolean isMinimumBalanceViolated() {
            return minimumBalanceViolated;
        }

        @Override
        public boolean isCreatingAccount() {
            return isCreatingAccount;
        }

        @Override
        public boolean isCreatedAccountBalanceFulfilled() {
            return createdAccountBalanceFulfilled;
        }
    }
}
