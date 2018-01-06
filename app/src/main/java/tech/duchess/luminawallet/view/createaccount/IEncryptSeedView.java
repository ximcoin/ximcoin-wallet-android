package tech.duchess.luminawallet.view.createaccount;

import android.support.annotation.Nullable;

public interface IEncryptSeedView {
    void showLoading(boolean isLoading);
    void showPasswordLengthError();
    void hidePasswordLengthError();
    void showPasswordMismatchError();
    void hidePasswordMismatchError();
    void setFinishEnabled(boolean isEnabled);
    void showSomethingWrongError();
    void finish();

    @Nullable
    String getPrimaryFieldContents();

    @Nullable
    String getSecondaryFieldContents();

    @Nullable
    String getSeed();
}
