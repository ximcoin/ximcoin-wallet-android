package com.ximcoin.ximwallet.view.account.send;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import com.ximcoin.ximwallet.dagger.module.BaseChildFragmentModule;
import com.ximcoin.ximwallet.dagger.scope.PerChildFragment;

@Module(includes = BaseChildFragmentModule.class)
abstract class SendConfirmationFragmentModule {
    @Binds
    @Named(BaseChildFragmentModule.CHILD_FRAGMENT)
    @PerChildFragment
    abstract Fragment fragment(SendConfirmationFragment sendConfirmationFragment);
}
