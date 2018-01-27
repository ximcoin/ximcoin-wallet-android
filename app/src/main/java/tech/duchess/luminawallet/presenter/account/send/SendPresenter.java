package tech.duchess.luminawallet.presenter.account.send;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Operation;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.api.HorizonApi;
import tech.duchess.luminawallet.model.persistence.HorizonDB;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.persistence.account.Balance;
import tech.duchess.luminawallet.model.repository.AccountRepository;
import tech.duchess.luminawallet.model.util.AssetUtil;
import tech.duchess.luminawallet.model.util.SeedEncryptionUtil;
import tech.duchess.luminawallet.model.util.TransactionUtil;
import tech.duchess.luminawallet.presenter.common.BasePresenter;
import tech.duchess.luminawallet.view.util.TextUtils;
import tech.duchess.luminawallet.view.util.ViewUtils;
import timber.log.Timber;

public class SendPresenter extends BasePresenter<SendContract.SendView>
        implements SendContract.SendPresenter {
    private static final String ACCOUNT_KEY = "SendPresenter.ACCOUNT_KEY";
    private static final int ADDRESS_LENGTH = 56;

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

        if (TextUtils.isEmpty(recipient) || recipient.length() < ADDRESS_LENGTH - 1) {
            view.showError(SendError.ADDRESS_INVALID);
            return;
        }

        if (TextUtils.isEmpty(amount) || Double.parseDouble(amount) <= 0) {
            view.showError(SendError.AMOUNT_GREATER_THAN_ZERO);
            return;
        }

        if (TextUtils.isEmpty(currency) || !assetCodeToIssuerMap.containsKey(currency)) {
            // This shouldn't really happen.
            view.showError(SendError.ADDRESS_UNSUPPORTED_CURRENCY);
            return;
        }

        pendingTransaction = new Transaction.Builder(sourceAccount)
                .addOperation(getPaymentOperation(sourceAccount, recipient, currency,
                        assetCodeToIssuerMap.get(currency), amount))
                .addMemo(getMemo(memo))
                .build();

        view.showPaymentSummaryConfirmation();
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

        accountRepository
                .getEncryptedSeed(pendingTransaction.getSourceAccount().getAccountId())
                .map(accountPrivateKey -> {
                    KeyPair signer = KeyPair.fromSecretSeed(SeedEncryptionUtil
                            .decryptSeed(accountPrivateKey.getEncryptedSeedPackage(), password));
                    pendingTransaction.sign(signer);
                    return pendingTransaction;
                })
                .flatMapCompletable(transaction ->
                        horizonApi.postTransaction(TransactionUtil.getEnvelopeXDRBase64(transaction)))
                .compose(schedulerProvider.completableScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doOnTerminate(() -> {
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
}
