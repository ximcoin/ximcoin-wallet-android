package tech.duchess.luminawallet.view.inflation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.functions.Action;

public interface InflationFlowManager {
    void onInflationSetComplete();
    void showLoading(boolean isLoading);
    void showBlockedLoading(@Nullable String message);
    void hideBlockedLoading(@Nullable String message,
                            boolean wasSuccess,
                            boolean immediate,
                            @Nullable Action action);
    void setTitle(@NonNull String title);
}
