package tech.duchess.luminawallet.view.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.presenter.account.AccountsContract;
import tech.duchess.luminawallet.view.common.BaseActivity;
import tech.duchess.luminawallet.view.common.ProgressOverlay;
import tech.duchess.luminawallet.view.contacts.ContactsActivity;
import tech.duchess.luminawallet.view.createaccount.AccountSourceReceiver;
import tech.duchess.luminawallet.view.createaccount.AddAccountSourceFragment;
import tech.duchess.luminawallet.view.createaccount.CreateAccountActivity;
import tech.duchess.luminawallet.view.inflation.InflationActivity;
import tech.duchess.luminawallet.view.util.ViewUtils;
import timber.log.Timber;

/**
 * Displays a Stellar Account, with its respective features (transactions, send, receive, etc...).
 * Will also display the option to create a new account if no account is found.
 */
public class AccountsActivity extends BaseActivity implements AccountsContract.AccountsView,
        NavigationView.OnNavigationItemSelectedListener, AccountSourceReceiver {
    private static int CREATE_ACCOUNT_REQUEST_CODE = 1;

    @BindView(R.id.progress_overlay)
    ProgressOverlay progressOverlay;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.solo_fragment_container)
    FrameLayout fragmentContainer;

    @BindView(R.id.view_pager)
    LockableViewPager viewPager;

    @BindView(R.id.account_header_view)
    AccountHeaderView accountHeaderView;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Inject
    AccountsContract.AccountsPresenter presenter;

    @NonNull
    private final List<String> accountList = new ArrayList<>();

    private int selectedTabColor;
    private int normalTabColor;
    private int disabledTabColor;
    private AccountFragmentPagerAdapter adapter;
    private boolean isMenuVisible = false;

    /**
     * Enumeration to represent the tabbed views. Note that the ordering here defines the
     * left-to-right ordering of the tabs in the views (meaning, ordinal here is important).
     */
    public enum AccountPerspective {
        BALANCES(R.drawable.balance_tab_icon),
        SEND(R.drawable.send_tab_icon),
        RECEIVE(R.drawable.receive_tab_icon),
        TRANSACTIONS(R.drawable.transactions_tab_icon);

        private final int tabIconRes;

        AccountPerspective(@DrawableRes int tabIconRes) {
            this.tabIconRes = tabIconRes;
        }

        public Drawable getTabIcon(@NonNull Context context) {
            return ContextCompat.getDrawable(context, tabIconRes);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts_activity);
        ButterKnife.bind(this);
        setupActionBar();
        initTabs();
        navigationView.setNavigationItemSelectedListener(this);
        presenter.start(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        presenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.pause();
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ViewUtils.whenNonNull(getSupportActionBar(), actionBar -> {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        });
    }

    private void initTabs() {
        viewPager.setOffscreenPageLimit(AccountPerspective.values().length);
        adapter = new AccountFragmentPagerAdapter(fragmentManager);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        for (AccountPerspective accountPerspective : AccountPerspective.values()) {
            tabLayout.getTabAt(accountPerspective.ordinal())
                    .setIcon(accountPerspective.getTabIcon(this));
        }

        selectedTabColor = ContextCompat.getColor(this, R.color.colorAccent);
        normalTabColor = ContextCompat.getColor(this, R.color.white_70);
        disabledTabColor = ContextCompat.getColor(this, R.color.white_20);
        // Initially make them enabled, for the sake of coloring.
        setTabsEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.saveState(outState);
    }

    @Override
    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showBlockedLoading(@Nullable String message) {
        progressOverlay.show(message);
    }

    // TODO: Blocked loading may get bit complex w/ the options. Turn into a POJO event we can pass
    // (likely via eventbus).
    @Override
    public void hideBlockedLoading(@Nullable String message, boolean wasSuccess, boolean immediate) {
        progressOverlay.hide(message, wasSuccess, immediate);
    }

    @Override
    public void showNoAccountFound() {
        updateUI(false, true, null);
        replaceFragment(R.id.solo_fragment_container, AddAccountSourceFragment.getInstance(true),
                true);
    }

    @Override
    public void showAccountsLoadFailure() {

    }

    @Override
    public void showAccountNavigationFailure() {

    }

    @Override
    public void showAccount(@NonNull Account account) {
        updateUI(true, false, account);
        Timber.d("Account on network: %s", account.getAccount_id());
    }

    @Override
    public void showAccountNotOnNetwork(@NonNull Account account) {
        updateUI(false, false, account);
        int receiveFragmentPosition = AccountPerspective.RECEIVE.ordinal();
        ViewUtils.setTabEnabled(tabLayout, true, receiveFragmentPosition);
        ViewUtils.whenNonNull(tabLayout.getTabAt(receiveFragmentPosition),
                TabLayout.Tab::select);
        tabLayout.setSelectedTabIndicatorColor(selectedTabColor);
        Timber.d("Account not on network: %s", account.getAccount_id());
    }

    @Override
    public void startCreateAccountFlow(boolean isImportingSeed) {
        startActivityForResult(CreateAccountActivity.createIntentForSeedGeneration(this,
                isImportingSeed), CREATE_ACCOUNT_REQUEST_CODE);
    }

    @Override
    public void navigateToCreateAccountFlow(boolean isNewToLumina) {
        startActivityForResult(CreateAccountActivity.createIntentForAccountSource(this,
                isNewToLumina), CREATE_ACCOUNT_REQUEST_CODE);
    }

    @Override
    public void navigateToContacts() {
        startActivity(ContactsActivity.createIntentForViewContacts(this));
    }

    @Override
    public void navigateToInflation(@NonNull String accountId) {
        startActivity(InflationActivity.createIntent(this, accountId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.accounts_menu, menu);
        menu.getItem(0).setVisible(isMenuVisible);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else if (item.getItemId() == R.id.set_inflation) {
            presenter.onUserNavigatedToInflation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_wallet) {
            presenter.onUserNavigatedToAddAccount();
        } else if (item.getItemId() == R.id.contacts) {
            presenter.onUserNavigatedToContacts();
        } else if (item.getGroupId() == R.id.wallet_menu_group) {
            presenter.onAccountNavigated(accountList.get(item.getOrder()));
        }

        drawerLayout.closeDrawers();
        // We'll update the checked item post-navigation.
        return false;
    }

    @Override
    public void onTransactionPosted(@NonNull Account account) {
        presenter.onTransactionPosted(account);
    }

    @Override
    public void updateForTransaction(@NonNull Account account) {
        accountHeaderView.setAccount(account);
        adapter.transactionPostedForAccount(account);
    }

    @Override
    public void updateAccountList(@NonNull List<Account> accounts) {
        accountList.clear();
        Menu menu = navigationView.getMenu();
        menu.removeGroup(R.id.wallet_menu_group);

        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            accountList.add(account.getAccount_id());
            menu.add(R.id.wallet_menu_group, Menu.NONE, i, account.getAccount_id());
        }

        menu.setGroupCheckable(R.id.wallet_menu_group, true, true);
    }

    private void updateSelectedAccount(@NonNull String selectedAccountId) {
        navigationView.getMenu().getItem(accountList.indexOf(selectedAccountId)).setChecked(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_ACCOUNT_REQUEST_CODE) {
            presenter.onAccountCreationReturned(resultCode == Activity.RESULT_OK);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateUI(boolean tabsEnabled,
                          boolean isSoloFragmentVisible,
                          @Nullable Account account) {
        updateTitle(account);
        setTabsEnabled(tabsEnabled);
        setSoloFragmentVisibility(isSoloFragmentVisible);
        ViewUtils.whenNonNull(account, acc -> updateSelectedAccount(acc.getAccount_id()));
        accountHeaderView.setAccount(account);
        adapter.setAccount(account);
        isMenuVisible = account != null && account.isOnNetwork();
        invalidateOptionsMenu();
    }

    private void setTabsEnabled(boolean tabsEnabled) {
        ViewUtils.setTabsEnabled(tabLayout, tabsEnabled);
        viewPager.setPagingEnabled(tabsEnabled);

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

    private void updateTitle(@Nullable Account account) {
        if (account == null) {
            toolbar.setTitle(R.string.app_name);
        } else {
            toolbar.setTitle(account.getAccount_id());
        }
    }

    public void onUserRequestedAccountCreation(boolean isImportingSeed) {
        presenter.onUserRequestAccountCreation(isImportingSeed);
    }
}
