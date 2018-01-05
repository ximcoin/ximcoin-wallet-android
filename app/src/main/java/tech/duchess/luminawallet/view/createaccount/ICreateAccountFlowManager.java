package tech.duchess.luminawallet.view.createaccount;

import android.support.annotation.NonNull;

public interface ICreateAccountFlowManager {
    void onSeedCreated(@NonNull String seed);
    void onAccountCreationCompleted();
    void showLoading(boolean isLoading);
    void setTitle(@NonNull String title);
}
