package com.ximcoin.ximwallet.presenter.nav;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface NavHeaderContract {
    interface NavHeaderView {
        void showConversionUnknown();
        void showConversion(@NonNull String conversion,
                            @NonNull String currency,
                            long epochSeconds);
        void showInfo();
        void showLoading(boolean isLoading);
        void showLoadError();
    }

    interface NavHeaderPresenter {
        void onViewAttached();
        void onUserRefreshConversion();
        void onUserRequestInfo();
        void onSaveInstanceState(@NonNull Bundle outState);
        void onViewStateRestored(@Nullable Bundle savedInstanceState);
        void onViewDetached();
    }
}
