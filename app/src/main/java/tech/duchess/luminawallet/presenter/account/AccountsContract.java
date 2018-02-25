package tech.duchess.luminawallet.presenter.account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.presenter.common.Presenter;

public interface AccountsContract {
    interface AccountsView {
        void showLoading(boolean isLoading);
        void showBlockedLoading(@Nullable String message);
        void hideBlockedLoading(@Nullable String message, boolean wasSuccess, boolean immediate);
        void showNoAccountFound();
        void showAccountsLoadFailure();
        void showAccountNavigationFailure();
        void showAccount(@NonNull Account account);
        void showAccountNotOnNetwork(@NonNull Account account);
        void startCreateAccountFlow(boolean isImportingSeed);
        void navigateToCreateAccountFlow(boolean isNewToLumina);
        void navigateToContacts();
        void navigateToInflation(@NonNull String accountId);
        void onTransactionPosted(@NonNull Account account);
        void updateForTransaction(@NonNull Account account);
        void updateAccountList(@NonNull List<Account> accounts);
    }

    interface AccountsPresenter extends Presenter {
        void onAccountCreationReturned(boolean didCreateNewAccount);
        void onUserRequestAccountCreation(boolean isImportingSeed);
        void onTransactionPosted(@NonNull Account account);
        void onAccountNavigated(@NonNull String accountId);
        void onUserNavigatedToAddAccount();
        void onUserNavigatedToContacts();
        void onUserNavigatedToInflation();
    }
}
