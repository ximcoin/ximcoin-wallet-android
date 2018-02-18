package tech.duchess.luminawallet.view.account;

import android.support.v7.app.AppCompatActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import tech.duchess.luminawallet.dagger.module.BaseActivityModule;
import tech.duchess.luminawallet.dagger.scope.PerActivity;
import tech.duchess.luminawallet.dagger.scope.PerFragment;
import tech.duchess.luminawallet.presenter.account.AccountsContract;
import tech.duchess.luminawallet.presenter.account.AccountsPresenterModule;
import tech.duchess.luminawallet.view.account.balance.BalancesFragment;
import tech.duchess.luminawallet.view.account.balance.BalancesFragmentModule;
import tech.duchess.luminawallet.view.account.receive.ReceiveFragment;
import tech.duchess.luminawallet.view.account.receive.ReceiveFragmentModule;
import tech.duchess.luminawallet.view.account.send.SendFragment;
import tech.duchess.luminawallet.view.account.send.SendFragmentModule;
import tech.duchess.luminawallet.view.account.transactions.TransactionsFragment;
import tech.duchess.luminawallet.view.account.transactions.TransactionsFragmentModule;
import tech.duchess.luminawallet.view.createaccount.AddAccountSourceFragment;
import tech.duchess.luminawallet.view.createaccount.AddAccountSourceFragmentModule;

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
}
