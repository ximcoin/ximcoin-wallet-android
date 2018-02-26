package tech.duchess.luminawallet.presenter.inflation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import tech.duchess.luminawallet.presenter.common.Presenter;

public interface InflationContract {
    interface InflationView {
        @Nullable
        String getAccountId();

        void showCurrentInflationAddress(@Nullable String inflationDestination);

        void showTransactionConfirmation(@NonNull InflationOperationSummary inflationOperationSummary);

        void showLoading(boolean isLoading);

        void showBlockedLoading(boolean isBuildingTransaction);

        void hideBlockedLoading(boolean wasBuildingTransaction, boolean wasSuccess);

        void showInsufficientFundsError(double minimumBalance);

        void showInflationError(@NonNull InflationPresenter.InflationError inflationError);
    }

    interface InflationPresenter extends Presenter {
        enum InflationError {
            ADDRESS_LENGTH,
            ADDRESS_PREFIX,
            ADDRESS_FORMAT,
            PASSWORD_LENGTH,
            ALREADY_SET
        }

        void onUserSetInflationDestination(@Nullable String destination);

        void onUserConfirmedTransaction(@Nullable String password);
    }
}
