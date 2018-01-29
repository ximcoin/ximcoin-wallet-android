package tech.duchess.luminawallet.view.account.transactions;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import tech.duchess.luminawallet.dagger.module.BaseFragmentModule;
import tech.duchess.luminawallet.dagger.scope.PerFragment;
import tech.duchess.luminawallet.presenter.account.transactions.TransactionsContract;
import tech.duchess.luminawallet.presenter.account.transactions.TransactionsPresenterModule;

@Module(includes = {BaseFragmentModule.class, TransactionsPresenterModule.class})
public abstract class TransactionsFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(TransactionsFragment transactionsFragment);

    @Binds
    @PerFragment
    abstract TransactionsContract.TransactionsView provideTransactionsView(TransactionsFragment transactionsFragment);
}
