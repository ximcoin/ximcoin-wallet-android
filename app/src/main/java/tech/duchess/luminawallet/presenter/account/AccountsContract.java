package tech.duchess.luminawallet.presenter.account;

import android.support.annotation.NonNull;

import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.presenter.common.Presenter;

public interface AccountsContract {
    interface AccountsView {
        void showLoading(boolean isLoading);
        void showNoAccountFound();
        void showAccountLoadFailure();
        void showAccount(@NonNull Account account);
        void showAccountNotOnNetwork(@NonNull Account account);
        void startCreateAccountFlow(boolean isImportingSeed);
    }

    interface AccountsPresenter extends Presenter {
        void onAccountCreationReturned(boolean didCreateNewAccount);
        void onUserRequestAccountCreation(boolean isImportingSeed);
    }
}
