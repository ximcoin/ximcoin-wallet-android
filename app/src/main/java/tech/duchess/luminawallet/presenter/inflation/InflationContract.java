package tech.duchess.luminawallet.presenter.inflation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import tech.duchess.luminawallet.presenter.common.Presenter;

public interface InflationContract {
    interface InflationView {
        @Nullable
        String getAccountId();

        void showCurrentInflationAddress(@Nullable String inflationDestination);

        void showOperationConfirmation();

        void showFeeLoading();

        void hideFeeLoading(double setInflationFee);

        void showFeeLoadFailed();

        void showInflationRemovalConfirmation();

        void showBlockedLoading();

        void hideBlockedLoading(boolean wasSuccess);

        void showNoAccountError();

        void showInsufficientFundsError(double minimumBalance);

        void showInflationError(@NonNull InflationPresenter.InflationError inflationError);
    }

    interface InflationPresenter extends Presenter {
        enum InflationError {
            ADDRESS_LENGTH,
            ADDRESS_PREFIX,
            ADDRESS_FORMAT,
            INFLATION_DESTINATION_NOT_EXIST
        }

        void onUserSetInflationDestination(@Nullable String destination);

        void onUserConfirmedInflationRemoval();

        void onUserConfirmedOperation(@Nullable String password);
    }
}
