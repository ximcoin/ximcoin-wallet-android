package tech.duchess.luminawallet.view.inflation;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import tech.duchess.luminawallet.dagger.module.BaseChildFragmentModule;
import tech.duchess.luminawallet.dagger.scope.PerChildFragment;

@Module(includes = BaseChildFragmentModule.class)
abstract class InflationConfirmationFragmentModule {
    @Binds
    @Named(BaseChildFragmentModule.CHILD_FRAGMENT)
    @PerChildFragment
    abstract Fragment fragment(InflationConfirmationFragment inflationConfirmationFragment);
}