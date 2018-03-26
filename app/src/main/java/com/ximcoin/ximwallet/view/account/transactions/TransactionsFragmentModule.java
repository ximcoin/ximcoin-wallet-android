package com.ximcoin.ximwallet.view.account.transactions;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import com.ximcoin.ximwallet.dagger.module.BaseFragmentModule;
import com.ximcoin.ximwallet.dagger.scope.PerFragment;
import com.ximcoin.ximwallet.presenter.account.transactions.TransactionsContract;
import com.ximcoin.ximwallet.presenter.account.transactions.TransactionsPresenterModule;

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
