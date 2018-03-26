package com.ximcoin.ximwallet.view.createaccount;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import com.ximcoin.ximwallet.dagger.module.BaseFragmentModule;
import com.ximcoin.ximwallet.dagger.scope.PerFragment;
import com.ximcoin.ximwallet.presenter.createaccount.EncryptSeedContract;
import com.ximcoin.ximwallet.presenter.createaccount.EncryptSeedPresenterModule;

@Module(includes = {BaseFragmentModule.class, EncryptSeedPresenterModule.class})
abstract class EncryptSeedFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(EncryptSeedFragment encryptSeedFragment);

    @Binds
    @PerFragment
    abstract EncryptSeedContract.EncryptSeedView encryptSeedView(EncryptSeedFragment encryptSeedFragment);
}
