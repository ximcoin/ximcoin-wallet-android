package com.ximcoin.ximwallet.view.account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.view.account.AccountsActivity.AccountPerspective;
import com.ximcoin.ximwallet.view.account.balance.BalancesFragment;
import com.ximcoin.ximwallet.view.account.receive.ReceiveFragment;
import com.ximcoin.ximwallet.view.account.send.SendFragment;
import com.ximcoin.ximwallet.view.account.transactions.TransactionsFragment;
import timber.log.Timber;

public class AccountFragmentPagerAdapter extends FragmentStatePagerAdapter {
    @NonNull
    private final SparseArray<Fragment> registeredFragments = new SparseArray<>();

    @Nullable
    private Account account;

    public AccountFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public void setAccount(@Nullable Account account) {
        this.account = account;
        for (int i = 0; i < registeredFragments.size(); i++) {
            Fragment fragment = registeredFragments.valueAt(i);
            if (fragment instanceof AccountPerspectiveView) {
                ((AccountPerspectiveView) fragment).setAccount(account);
            }
        }
    }

    public void transactionPostedForAccount(@NonNull Account account) {
        if (this.account == null || !this.account.getAccount_id().equals(account.getAccount_id())) {
            return;
        }

        for (int i = 0; i < registeredFragments.size(); i++) {
            Fragment fragment = registeredFragments.valueAt(i);
            if (fragment instanceof AccountPerspectiveView) {
                ((AccountPerspectiveView) fragment).transactionPostedForAccount(account);
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        AccountPerspective accountPerspective = AccountPerspective.values()[position];
        switch (accountPerspective) {
            case BALANCES:
                return BalancesFragment.newInstance(account);
            case SEND:
                return SendFragment.newInstance(account);
            case RECEIVE:
                return ReceiveFragment.newInstance(account);
            case TRANSACTIONS:
                return TransactionsFragment.newInstance(account);
            default:
                Timber.e("Unhandled account perspective: %s", accountPerspective.name());
                return null;
        }
    }

    @Override
    public int getCount() {
        return AccountPerspective.values().length;
    }

    @Override
    @NonNull
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Nullable
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}