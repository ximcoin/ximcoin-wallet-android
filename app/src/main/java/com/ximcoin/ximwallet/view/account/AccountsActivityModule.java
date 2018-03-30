package com.ximcoin.ximwallet.view.account;

import android.support.v7.app.AppCompatActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import com.ximcoin.ximwallet.dagger.module.BaseActivityModule;
import com.ximcoin.ximwallet.dagger.scope.PerActivity;
import com.ximcoin.ximwallet.dagger.scope.PerFragment;
import com.ximcoin.ximwallet.presenter.account.AccountsContract;
import com.ximcoin.ximwallet.presenter.account.AccountsPresenterModule;
import com.ximcoin.ximwallet.view.account.balance.BalancesFragment;
import com.ximcoin.ximwallet.view.account.balance.BalancesFragmentModule;
import com.ximcoin.ximwallet.view.account.receive.ReceiveFragment;
import com.ximcoin.ximwallet.view.account.receive.ReceiveFragmentModule;
import com.ximcoin.ximwallet.view.account.send.SendFragment;
import com.ximcoin.ximwallet.view.account.send.SendFragmentModule;
import com.ximcoin.ximwallet.view.account.transactions.TransactionsFragment;
import com.ximcoin.ximwallet.view.account.transactions.TransactionsFragmentModule;
import com.ximcoin.ximwallet.view.createaccount.AddAccountSourceFragment;
import com.ximcoin.ximwallet.view.createaccount.AddAccountSourceFragmentModule;
import com.ximcoin.ximwallet.view.exportidweb.ExportIdWebviewActivity;

@Module(includes = {BaseActivityModule.class, AccountsPresenterModule.class})
public abstract class AccountsActivityModule {
    @Binds
    @PerActivity
    abstract AccountsContract.AccountsView provideAccountsView(AccountsActivity accountsActivity);

    @Binds
    @PerActivity
    abstract AppCompatActivity appCompatActivity(AccountsActivity accountsActivity);

    @PerFragment
    @ContributesAndroidInjector(modules = AddAccountSourceFragmentModule.class)
    abstract AddAccountSourceFragment addAccountSourceFragmentInjector();

    @PerFragment
    @ContributesAndroidInjector(modules = BalancesFragmentModule.class)
    abstract BalancesFragment balancesFragmentInjector();

    @PerFragment
    @ContributesAndroidInjector(modules = ReceiveFragmentModule.class)
    abstract ReceiveFragment receiveFragmentInjector();

    @PerFragment
    @ContributesAndroidInjector(modules = SendFragmentModule.class)
    abstract SendFragment sendFragmentInjector();

    @PerFragment
    @ContributesAndroidInjector(modules = TransactionsFragmentModule.class)
    abstract TransactionsFragment transactionsFragmentInjector();

    @PerFragment
    @ContributesAndroidInjector(modules = TrustXimFragmentModule.class)
    abstract TrustXimFragment trustXimFragmentInjector();
}
