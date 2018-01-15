package tech.duchess.luminawallet.view.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
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
import tech.duchess.luminawallet.view.util.ViewBindingUtils;
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

    @BindView(R.id.solo_fragment_container)
    FrameLayout fragmentContainer;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.account_header_view)
    AccountHeaderView accountHeaderView;

    @Inject
    AccountsPresenter presenter;

    private int selectedTabColor;
    private int normalTabColor;
    private int disabledTabColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        initTabs();

        LuminaWalletApp.getInstance()
                .getAppComponent()
                .plus(new ActivityLifecycleModule(this))
                .inject(this);

        presenter.attachView(this, savedInstanceState == null);
    }

    private void initTabs() {
        selectedTabColor = ContextCompat.getColor(this, R.color.white);
        normalTabColor = ContextCompat.getColor(this, R.color.white_70);
        disabledTabColor = ContextCompat.getColor(this, R.color.white_20);

        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(this,
                R.drawable.balance_tab_icon)));
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(this,
                R.drawable.send_tab_icon)));
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(this,
                R.drawable.receive_tab_icon)));
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(this,
                R.drawable.transactions_tab_icon)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showNoAccountFound() {
        updateUI(false, true, null);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.solo_fragment_container, new NoAccountFoundFragment())
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void showAccountLoadFailure() {

    }

    @Override
    public void showAccount(@NonNull Account account) {
        updateUI(true, false, account);
        Timber.d("Account on network: %s", account.getAccount_id());
    }

    @Override
    public void showAccountNotOnNetwork(@NonNull Account account) {
        updateUI(false, true, account);
        Timber.d("Account not on network: %s", account.getAccount_id());
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

    private void updateUI(boolean tabsEnabled,
                          boolean isSoloFragmentVisible,
                          @Nullable Account account) {
        setTabsEnabled(tabsEnabled);
        setSoloFragmentVisibility(isSoloFragmentVisible);
        updateAccountHeader(account);
    }

    private void setTabsEnabled(boolean tabsEnabled) {
        ViewBindingUtils.setTabsEnabled(tabLayout, tabsEnabled);

        if (tabsEnabled) {
            tabLayout.setTabTextColors(normalTabColor, selectedTabColor);
            tabLayout.setSelectedTabIndicatorColor(selectedTabColor);
        } else {
            tabLayout.setTabTextColors(disabledTabColor, disabledTabColor);
            tabLayout.setSelectedTabIndicatorColor(disabledTabColor);
        }
    }

    private void setSoloFragmentVisibility(boolean isVisible) {
        if (isVisible) {
            viewPager.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);
        } else {
            viewPager.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
        }
    }

    private void updateAccountHeader(@Nullable Account account) {
        accountHeaderView.setAccount(account);
    }
}
