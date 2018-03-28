package com.ximcoin.ximwallet.view.createaccount;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import com.ximcoin.ximwallet.dagger.module.BaseFragmentModule;
import com.ximcoin.ximwallet.dagger.scope.PerFragment;
import com.ximcoin.ximwallet.presenter.createaccount.AddAccountSourceContract;
import com.ximcoin.ximwallet.presenter.createaccount.AddAccountSourcePresenterModule;

@Module(includes = {BaseFragmentModule.class, AddAccountSourcePresenterModule.class})
public abstract class AddAccountSourceFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(AddAccountSourceFragment addAccountSourceFragment);

    @Binds
    @PerFragment
    abstract AddAccountSourceContract.AddAccountSourceView addAccountSourceView(AddAccountSourceFragment fragment);
}
