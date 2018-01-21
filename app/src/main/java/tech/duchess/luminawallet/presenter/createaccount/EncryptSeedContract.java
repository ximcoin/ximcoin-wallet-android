package tech.duchess.luminawallet.presenter.createaccount;

import android.support.annotation.Nullable;
import tech.duchess.luminawallet.presenter.common.Presenter;

public interface EncryptSeedContract {
    interface EncryptSeedView {
        void setFinishEnabled(boolean isEnabled);
        void hidePasswordLengthError();
        void hidePasswordMismatchError();
        void showPasswordLengthError();
        void showPasswordMismatchError();
        void showSomethingWrongError();
        void showLoading(boolean isLoading);
        void finish();
    }

    interface EncryptSeedPresenter extends Presenter {
        void onUserFinished(@Nullable String password,
                            @Nullable String passwordValidation,
                            @Nullable String seed);
        void onPasswordContentsChanged();
    }
}
