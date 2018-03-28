package com.ximcoin.ximwallet.presenter.createaccount;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ximcoin.ximwallet.model.fees.Fees;
import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.presenter.common.Presenter;

public interface AddAccountSourceContract {
    interface AddAccountSourceView {
        void showLoading(boolean isLoading);
        void showLoadFailure();
        void showNoAccount();
        void showAccountLacksXimTrust(@NonNull Fees fees, @NonNull Account account);
        void startCreateAccountFlow(boolean isImportingSeed);

        @Nullable
        Account getAccount();
    }

    interface AddAccountSourcePresenter extends Presenter {
        void onUserRequestCreateAccount();
        void onUserRequestImportAccount();
        void onUserRequestExportIdLogin();
        void onUserRequestFundXim();
    }
}
