package tech.duchess.luminawallet.presenter.account.send;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.presenter.common.Presenter;

public interface SendContract {
    interface SendView {
        void showLoading(boolean isLoading);

        void showError(@NonNull SendPresenter.SendError error);

        void showNewAccountWarning();

        void showPaymentSummaryConfirmation();

        void showNoAccount();

        void setAvailableCurrencies(List<String> currencySelection);

        void clearForm();

        void showTransactionSuccess(@NonNull Account account);
    }

    interface SendPresenter extends Presenter {
        enum SendError {
            INSUFFICIENT_FUNDS,
            AMOUNT_GREATER_THAN_ZERO,
            ADDRESS_INVALID,
            ADDRESS_UNSUPPORTED_CURRENCY,
            PASSWORD_INVALID,
            TRANSACTION_FAILED,
        }

        void onUserSendPayment(@Nullable String recipient,
                               @Nullable String amount,
                               @Nullable String currency,
                               @Nullable String memo);

        void onUserConfirmPayment(@Nullable String password);

        void onAccountUpdated(@Nullable Account account);
    }
}
