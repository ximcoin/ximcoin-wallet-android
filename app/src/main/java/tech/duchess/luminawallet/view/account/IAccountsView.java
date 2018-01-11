package tech.duchess.luminawallet.view.account;

import android.support.annotation.NonNull;

import tech.duchess.luminawallet.model.persistence.account.Account;

public interface IAccountsView {
    void showLoading(boolean isLoading);
    void showNoAccountFound();
    void showAccountLoadFailure();
    void showAccount(@NonNull Account account);
    void showAccountNotOnNetwork(@NonNull String publicAddress);
}
