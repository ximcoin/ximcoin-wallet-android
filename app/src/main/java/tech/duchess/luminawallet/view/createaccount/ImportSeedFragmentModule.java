package tech.duchess.luminawallet.view.createaccount;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import tech.duchess.luminawallet.dagger.module.BaseFragmentModule;
import tech.duchess.luminawallet.dagger.scope.PerFragment;
import tech.duchess.luminawallet.presenter.createaccount.ImportSeedContract;
import tech.duchess.luminawallet.presenter.createaccount.ImportSeedPresenterModule;

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
