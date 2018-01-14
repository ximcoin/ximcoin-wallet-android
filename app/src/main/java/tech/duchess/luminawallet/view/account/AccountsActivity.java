package tech.duchess.luminawallet.view.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.duchess.luminawallet.LuminaWalletApp;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.dagger.module.ActivityLifecycleModule;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.presenter.account.AccountsPresenter;
import tech.duchess.luminawallet.view.createaccount.CreateAccountActivity;
import timber.log.Timber;

/**
 * Displays a Stellar Account, with its respective features (transactions, send, receive, etc...).
 * Will also display the option to create a new account if no account is found.
 */
public class AccountsActivity extends RxAppCompatActivity implements IAccountsView {
    private static final int CREATE_ACCOUNT_REQUEST_CODE = 1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @Inject
    AccountsPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts_activity_v2);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        LuminaWalletApp.getInstance()
                .getAppComponent()
                .plus(new ActivityLifecycleModule(this))
                .inject(this);

        presenter.attachView(this, savedInstanceState == null);

        tabLayout.addTab(tabLayout.newTab().setText("Balances"));
        tabLayout.addTab(tabLayout.newTab().setText("Send"));
        tabLayout.addTab(tabLayout.newTab().setText("Receive"));
        tabLayout.addTab(tabLayout.newTab().setText("Transactions"));

        startCreateAccountActivity(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    private void displayAccountFragments(Account account) {
    }

    private void displayCreateAccountFragment() {
        /*getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new NoAccountFoundFragment())
                .commit();
        getSupportFragmentManager().executePendingTransactions();*/
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showNoAccountFound() {
        setBottomNavVisibility(false);
        displayCreateAccountFragment();
    }

    @Override
    public void showAccountLoadFailure() {
        setBottomNavVisibility(false);
    }

    @Override
    public void showAccount(@NonNull Account account) {
        setBottomNavVisibility(true);
        Timber.d("Account on network: %s", account.getAccount_id());
    }

    @Override
    public void showAccountNotOnNetwork(@NonNull String publicAddress) {
        setBottomNavVisibility(false);
        Timber.d("Account not on network: %s", publicAddress);
    }

    @Override
    public void startCreateAccountActivity(boolean isImportingSeed) {
        if (!isImportingSeed) {
            startActivityForResult(new Intent(this, CreateAccountActivity.class),
                    CREATE_ACCOUNT_REQUEST_CODE);
        } else {
            //TODO
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_ACCOUNT_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            presenter.refreshAccounts();
        }
    }

    private void setBottomNavVisibility(boolean isVisible) {
        // bottomNavigationView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
