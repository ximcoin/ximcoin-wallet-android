package com.ximcoin.ximwallet.presenter.account.send;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.presenter.common.Presenter;

public interface SendContract {
    interface SendView {
        void showBlockedLoading(boolean isLoading,
                                boolean isBuildingTransaction,
                                boolean wasSuccess);

        void showError(@NonNull SendPresenter.SendError error);

        void showConfirmation(@NonNull TransactionSummary transactionSummary);

        void showNoAccount();

        void setAvailableCurrencies(List<String> currencySelection);

        void clearForm();

        void showTransactionSuccess(@NonNull Account account, @NonNull String destination);
    }

    interface SendPresenter extends Presenter {
        enum SendError {
            /**
             * Denotes a user attempting to send more than their available balance (irrespective of
             * asset type).
             */
            INSUFFICIENT_FUNDS,
            /**
             * Denotes a user attempting to send a negative balance.
             */
            AMOUNT_GREATER_THAN_ZERO,
            /**
             * Denotes a user attempting to send to an address of improper length.
             */
            ADDRESS_BAD_LENGTH,
            /**
             * Denotes a user attempting to send to a malformed address.
             */
            ADDRESS_INVALID,
            /**
             * Denotes a user attempting to send to an address of improper prefix.
             */
            ADDRESS_BAD_PREFIX,
            /**
             * Denotes a user attempting to send money to themselves (are you crazy?).
             */
            DEST_SAME_AS_SOURCE,
            /**
             * Denotes a user attempting to send an asset to an account that does not have the
             * respective trust set.
             */
            ADDRESS_UNSUPPORTED_CURRENCY,
            /**
             * Denotes a user attempting to send a non-native asset to an account that is not on the
             * network.
             */
            ADDRESS_DOES_NOT_EXIST,
            /**
             * Denotes a user providing the incorrect password.
             */
            PASSWORD_INVALID
        }

        void onUserSendPayment(@Nullable String recipient,
                               @Nullable String amount,
                               @Nullable String currency,
                               @Nullable String memo);

        void onUserConfirmPayment(@Nullable String password);

        void onAccountUpdated(@Nullable Account account);
    }
}
