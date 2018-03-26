package com.ximcoin.ximwallet.view.inflation;

import android.support.v7.app.AppCompatActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import com.ximcoin.ximwallet.dagger.module.BaseActivityModule;
import com.ximcoin.ximwallet.dagger.scope.PerActivity;
import com.ximcoin.ximwallet.dagger.scope.PerFragment;

@Module(includes = BaseActivityModule.class)
public abstract class InflationActivityModule {
    @Binds
    @PerActivity
    abstract AppCompatActivity appCompatActivity(InflationActivity inflationActivity);

    @PerFragment
    @ContributesAndroidInjector(modules = InflationFragmentModule.class)
    abstract InflationFragment inflationFragmentInjector();
}
