package com.ximcoin.ximwallet.view.inflation;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import com.ximcoin.ximwallet.dagger.module.BaseFragmentModule;
import com.ximcoin.ximwallet.dagger.scope.PerChildFragment;
import com.ximcoin.ximwallet.dagger.scope.PerFragment;
import com.ximcoin.ximwallet.presenter.inflation.InflationContract;
import com.ximcoin.ximwallet.presenter.inflation.InflationPresenterModule;

@Module(includes = {BaseFragmentModule.class, InflationPresenterModule.class})
abstract class InflationFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(InflationFragment inflationFragment);

    @Binds
    @PerFragment
    abstract InflationContract.InflationView provideInflationView(InflationFragment inflationFragment);

    @PerChildFragment
    @ContributesAndroidInjector(modules = InflationConfirmationFragmentModule.class)
    abstract InflationConfirmationFragment inflationConfirmationFragmentInjector();
}
