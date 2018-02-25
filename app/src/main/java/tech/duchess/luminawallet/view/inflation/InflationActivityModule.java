package tech.duchess.luminawallet.view.inflation;

import android.support.v7.app.AppCompatActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import tech.duchess.luminawallet.dagger.module.BaseActivityModule;
import tech.duchess.luminawallet.dagger.scope.PerActivity;
import tech.duchess.luminawallet.dagger.scope.PerFragment;

@Module(includes = BaseActivityModule.class)
public abstract class InflationActivityModule {
    @Binds
    @PerActivity
    abstract AppCompatActivity appCompatActivity(InflationActivity inflationActivity);

    @PerFragment
    @ContributesAndroidInjector(modules = InflationFragmentModule.class)
    abstract InflationFragment inflationFragmentInjector();
}
