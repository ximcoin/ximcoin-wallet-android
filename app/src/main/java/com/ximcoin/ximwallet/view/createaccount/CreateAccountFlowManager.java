package com.ximcoin.ximwallet.view.createaccount;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface CreateAccountFlowManager {
    void onSeedCreated(@NonNull String seed);
    void onAccountCreationCompleted();
    void showLoading(boolean isLoading);
    void setTitle(@NonNull String title);
    void showBlockedLoading(@Nullable String message);
    void hideBlockedLoading();
}
