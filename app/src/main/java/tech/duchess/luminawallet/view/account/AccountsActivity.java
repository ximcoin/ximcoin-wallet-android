package tech.duchess.luminawallet.view.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.presenter.account.AccountsContract;
import tech.duchess.luminawallet.view.common.BaseActivity;
import tech.duchess.luminawallet.view.common.ProgressOverlay;
import tech.duchess.luminawallet.view.createaccount.CreateAccountActivity;
import tech.duchess.luminawallet.view.util.ViewUtils;
import timber.log.Timber;

/**
 * Displays a Stellar Account, with its respective features (transactions, send, receive, etc...).
 * Will also display the option to create a new account if no account is found.
 */
public class AccountsActivity extends BaseActivity implements AccountsContract.AccountsView {
    private static final int CREATE_ACCOUNT_REQUEST_CODE = 1;

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

    @Inject
    AccountsContract.AccountsPresenter presenter;

    private int selectedTabColor;
    private int normalTabColor;
    private int disabledTabColor;
    private AccountFragmentPagerAdapter adapter;

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
        setSupportActionBar(toolbar);
        initTabs();
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

    private void initTabs() {
        viewPager.setOffscreenPageLimit(AccountPerspective.values().length);
        adapter = new AccountFragmentPagerAdapter(fragmentManager);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        for (AccountPerspective accountPerspective : AccountPerspective.values()) {
            tabLayout.getTabAt(accountPerspective.ordinal()).setIcon(accountPerspective.getTabIcon(this));
        }

        selectedTabColor = ContextCompat.getColor(this, R.color.white);
        normalTabColor = ContextCompat.getColor(this, R.color.white_70);
        disabledTabColor = ContextCompat.getColor(this, R.color.white_20);
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
        replaceFragment(R.id.solo_fragment_container, new NoAccountFoundFragment(), true);
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
        if (!isImportingSeed) {
            startActivityForResult(new Intent(this, CreateAccountActivity.class),
                    CREATE_ACCOUNT_REQUEST_CODE);
        } else {
            //TODO
        }
    }

    @Override
    public void onTransactionPosted(@NonNull Account account) {
        presenter.onTransactionPosted(account);
    }

    @Override
    public void updateForTransaction(@NonNull Account account) {
        accountHeaderView.setAccount(account);
        adapter.setAccount(account);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_ACCOUNT_REQUEST_CODE) {
            presenter.onAccountCreationReturned(resultCode == Activity.RESULT_OK);
        }
    }

    private void updateUI(boolean tabsEnabled,
                          boolean isSoloFragmentVisible,
                          @Nullable Account account) {
        updateTitle(account);
        setTabsEnabled(tabsEnabled);
        setSoloFragmentVisibility(isSoloFragmentVisible);
        accountHeaderView.setAccount(account);
        adapter.setAccount(account);
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
            toolbar.setTitle(getString(R.string.account_id_label) + account.getAccount_id());
        }
    }

    void onUserRequestedAccountCreation(boolean isImportingSeed) {
        presenter.onUserRequestAccountCreation(isImportingSeed);
    }
}
