package com.ximcoin.ximwallet.view.contacts;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import com.ximcoin.ximwallet.dagger.module.BaseFragmentModule;
import com.ximcoin.ximwallet.dagger.scope.PerFragment;
import com.ximcoin.ximwallet.presenter.contacts.ViewContactContract;
import com.ximcoin.ximwallet.presenter.contacts.ViewContactPresenterModule;

@Module(includes = {BaseFragmentModule.class, ViewContactPresenterModule.class})
abstract class ViewContactFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(ViewContactFragment viewContactFragment);

    @Binds
    @PerFragment
    abstract ViewContactContract.ViewContactView provideViewContactView(ViewContactFragment viewContactFragment);
}
