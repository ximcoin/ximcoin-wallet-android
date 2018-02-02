package tech.duchess.luminawallet.presenter.account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.presenter.common.Presenter;

public interface AccountsContract {
    interface AccountsView {
        void showLoading(boolean isLoading);
        void showBlockedLoading(@Nullable String message);
        void hideBlockedLoading(@Nullable String message, boolean wasSuccess, boolean immediate);
        void showNoAccountFound();
        void showAccountLoadFailure();
        void showAccount(@NonNull Account account);
        void showAccountNotOnNetwork(@NonNull Account account);
        void startCreateAccountFlow(boolean isImportingSeed);
        void onTransactionPosted(@NonNull Account account);
        void updateForTransaction(@NonNull Account account);
    }

    interface AccountsPresenter extends Presenter {
        void onAccountCreationReturned(boolean didCreateNewAccount);
        void onUserRequestAccountCreation(boolean isImportingSeed);
        void onTransactionPosted(@NonNull Account account);
    }
}
