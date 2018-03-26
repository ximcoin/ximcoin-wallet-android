package com.ximcoin.ximwallet.view.createaccount;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import com.ximcoin.ximwallet.dagger.module.BaseFragmentModule;
import com.ximcoin.ximwallet.dagger.scope.PerFragment;
import com.ximcoin.ximwallet.presenter.createaccount.ImportSeedContract;
import com.ximcoin.ximwallet.presenter.createaccount.ImportSeedPresenterModule;

@Module(includes = {BaseFragmentModule.class, ImportSeedPresenterModule.class})
abstract class ImportSeedFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(ImportSeedFragment importSeedFragment);

    @Binds
    @PerFragment
    abstract ImportSeedContract.ImportSeedView importSeedView(ImportSeedFragment importSeedFragment);
}
