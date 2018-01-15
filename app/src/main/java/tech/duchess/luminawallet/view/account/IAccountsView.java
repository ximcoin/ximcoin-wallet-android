package tech.duchess.luminawallet.view.account;

import android.support.annotation.NonNull;

import tech.duchess.luminawallet.model.persistence.account.Account;

public interface IAccountsView {

    // For AccountsPresenter
    void showLoading(boolean isLoading);
    void showNoAccountFound();
    void showAccountLoadFailure();
    void showAccount(@NonNull Account account);
    void showAccountNotOnNetwork(@NonNull Account account);

    // For child fragments
    void startCreateAccountActivity(boolean isImportingSeed);
}
