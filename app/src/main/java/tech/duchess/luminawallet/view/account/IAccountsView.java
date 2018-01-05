package tech.duchess.luminawallet.view.account;

import android.support.annotation.NonNull;

import java.util.List;

import tech.duchess.luminawallet.model.persistence.account.Account;

public interface IAccountsView {
    void showAccounts(@NonNull List<Account> accountList);

    void showLoadError();

    void showLoading(boolean isLoading);
}
