package com.ximcoin.ximwallet.presenter.account;

import android.support.annotation.Nullable;

import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.presenter.common.Presenter;

public interface TrustXimContract {
    interface TrustXimView {
        void showTrustTransactionInProgress();
        void hideTrustTransactionProgress(boolean wasSuccess);
        void showTransactionBuildFailure();
        @Nullable
        Account getCurrentAccount();
        void showPasswordInvalidError();

    }

    interface TrustXimPresenter extends Presenter {
        void trustXim(@Nullable String password);
    }
}
