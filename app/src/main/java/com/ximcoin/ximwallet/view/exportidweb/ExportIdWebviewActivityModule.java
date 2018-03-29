package com.ximcoin.ximwallet.view.exportidweb;

import android.support.v7.app.AppCompatActivity;

import com.ximcoin.ximwallet.dagger.module.BaseActivityModule;
import com.ximcoin.ximwallet.dagger.scope.PerActivity;

import dagger.Binds;
import dagger.Module;

@Module(includes = BaseActivityModule.class)
public abstract class ExportIdWebviewActivityModule {
    @Binds
    @PerActivity
    abstract AppCompatActivity appCompatActivity(ExportIdWebviewActivity exportIdWebviewActivity);
}