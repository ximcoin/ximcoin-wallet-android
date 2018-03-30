package com.ximcoin.ximwallet.view.account;

import android.support.v4.app.Fragment;

import com.ximcoin.ximwallet.dagger.module.BaseFragmentModule;
import com.ximcoin.ximwallet.dagger.scope.PerFragment;
import com.ximcoin.ximwallet.presenter.account.TrustXimContract;
import com.ximcoin.ximwallet.presenter.account.TrustXimPresenterModule;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;

@Module(includes = {BaseFragmentModule.class, TrustXimPresenterModule.class})
abstract class TrustXimFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(TrustXimFragment trustXimFragment);

    @Binds
    @PerFragment
    abstract TrustXimContract.TrustXimView provideTrustXimView(TrustXimFragment fragment);
}
