package tech.duchess.luminawallet.view.createaccount;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import tech.duchess.luminawallet.dagger.module.BaseFragmentModule;
import tech.duchess.luminawallet.dagger.scope.PerFragment;
import tech.duchess.luminawallet.presenter.createaccount.EncryptSeedContract;
import tech.duchess.luminawallet.presenter.createaccount.EncryptSeedPresenterModule;

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
