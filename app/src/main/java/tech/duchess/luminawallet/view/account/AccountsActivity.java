package tech.duchess.luminawallet.view.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

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
import tech.duchess.luminawallet.view.createaccount.CreateAccountActivity;

/**
 * Displays a Stellar Account, with it's respective features (transactions, send, receive, etc...).
 * Will also display the option to create a new account if no account is found.
 */
public class AccountsActivity extends RxAppCompatActivity implements IAccountsView {
    private static final String LOADED_ACCOUNTS_KEY = "AccountsActivity.LOADED_ACCOUNTS_KEY";
    private static final int CREATE_ACCOUNT_REQUEST_CODE = 1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Inject
    AccountsPresenter presenter;

    private boolean hasLoadedAccounts = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_activity);
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
        startActivityForResult(new Intent(this, CreateAccountActivity.class),
                CREATE_ACCOUNT_REQUEST_CODE);
        /*getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new NoAccountFoundFragment())
                .commit();
        getSupportFragmentManager().executePendingTransactions();*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_ACCOUNT_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            presenter.loadUI(true);
        }
    }

    @Override
    public void showLoadError() {

    }

    @Override
    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
