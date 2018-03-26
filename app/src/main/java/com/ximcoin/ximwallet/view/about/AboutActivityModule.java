package com.ximcoin.ximwallet.view.about;

import android.support.v7.app.AppCompatActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import com.ximcoin.ximwallet.dagger.module.BaseActivityModule;
import com.ximcoin.ximwallet.dagger.scope.PerActivity;
import com.ximcoin.ximwallet.dagger.scope.PerFragment;

@Module(includes = BaseActivityModule.class)
public abstract class AboutActivityModule {
    @Binds
    @PerActivity
    abstract AppCompatActivity appCompatActivity(AboutActivity aboutActivity);

    @PerFragment
    @ContributesAndroidInjector(modules = AboutFragmentModule.class)
    abstract AboutFragment aboutFragmentInjector();
}
