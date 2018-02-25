package tech.duchess.luminawallet.view.inflation;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import tech.duchess.luminawallet.dagger.module.BaseFragmentModule;
import tech.duchess.luminawallet.dagger.scope.PerFragment;
import tech.duchess.luminawallet.presenter.inflation.InflationContract;
import tech.duchess.luminawallet.presenter.inflation.InflationPresenterModule;

@Module(includes = {BaseFragmentModule.class, InflationPresenterModule.class})
abstract class InflationFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(InflationFragment inflationFragment);

    @Binds
    @PerFragment
    abstract InflationContract.InflationView provideInflationView(InflationFragment inflationFragment);
}
