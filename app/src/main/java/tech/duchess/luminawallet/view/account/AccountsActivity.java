package tech.duchess.luminawallet.view.account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.duchess.luminawallet.LuminaWalletApp;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.dagger.module.ActivityLifecycleModule;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.presenter.account.AccountsPresenter;

/**
 * Displays a Stellar Account, with it's respective features (transactions, send, receive, etc...).
 * Will also display the option to create a new account if no account is found.
 */
public class AccountsActivity extends RxAppCompatActivity implements IAccountsView {
    private static final String LOADED_ACCOUNTS_KEY = "AccountsActivity.LOADED_ACCOUNTS_KEY";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fragment_container)
    ViewGroup fragmentContainer;

    @Inject
    AccountsPresenter presenter;

    private boolean hasLoadedAccounts = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        LuminaWalletApp.getInstance()
                .getAppComponent()
                .plus(new ActivityLifecycleModule(this))
                .inject(this);

        if (savedInstanceState != null) {
            hasLoadedAccounts = savedInstanceState.getBoolean(LOADED_ACCOUNTS_KEY);
        }

        presenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onViewDetached();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasLoadedAccounts) {
            presenter.loadUI(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(LOADED_ACCOUNTS_KEY, hasLoadedAccounts);
    }

    @Override
    public void showAccounts(@NonNull List<Account> accountList) {
        hasLoadedAccounts = true;

        if (accountList.isEmpty()) {
            displayCreateAccountFragment();
            return;
        }

        AccountFragment accountFragment = (AccountFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (accountFragment == null) {
            initAccountFragment(accountList.get(0));
        } else {
            accountFragment.updateAccount(accountList.get(0));
        }
    }

    private void initAccountFragment(Account account) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, AccountFragment.newInstance(account,
                        AccountFragment.AccountPerspective.BALANCES))
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void displayCreateAccountFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CreateAccountFragment())
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void showLoadError() {

    }

    @Override
    public void setLoading(boolean isLoading) {

    }
}
