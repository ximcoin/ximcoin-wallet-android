package com.ximcoin.ximwallet.view.createaccount;

import android.support.annotation.NonNull;

public interface CreateAccountFlowManager {
    void onSeedCreated(@NonNull String seed);
    void onAccountCreationCompleted();
    void showLoading(boolean isLoading);
    void setTitle(@NonNull String title);
}
