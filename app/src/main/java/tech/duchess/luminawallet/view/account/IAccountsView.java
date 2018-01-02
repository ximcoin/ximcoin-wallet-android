package tech.duchess.luminawallet.view.account;

import android.support.annotation.NonNull;

import java.util.List;

import tech.duchess.luminawallet.model.persistence.account.Account;

public interface IAccountsView {
    public void showAccounts(@NonNull List<Account> accountList);

    public void showLoadError();

    public void setLoading(boolean isLoading);
}
