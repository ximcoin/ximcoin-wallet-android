package com.ximcoin.ximwallet.view.account.send;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import com.ximcoin.ximwallet.dagger.module.BaseFragmentModule;
import com.ximcoin.ximwallet.dagger.scope.PerChildFragment;
import com.ximcoin.ximwallet.dagger.scope.PerFragment;
import com.ximcoin.ximwallet.presenter.account.send.SendContract;
import com.ximcoin.ximwallet.presenter.account.send.SendPresenterModule;

@Module(includes = {BaseFragmentModule.class, SendPresenterModule.class})
public abstract class SendFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(SendFragment sendFragment);

    @Binds
    @PerFragment
    abstract SendContract.SendView provideSendView(SendFragment sendFragment);

    @PerChildFragment
    @ContributesAndroidInjector(modules = SendConfirmationFragmentModule.class)
    abstract SendConfirmationFragment sendConfirmationFragmentInjector();
}
