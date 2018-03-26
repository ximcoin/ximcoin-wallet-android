package com.ximcoin.ximwallet.view.createaccount;

import android.support.v7.app.AppCompatActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import com.ximcoin.ximwallet.dagger.module.BaseActivityModule;
import com.ximcoin.ximwallet.dagger.scope.PerActivity;
import com.ximcoin.ximwallet.dagger.scope.PerFragment;

@Module(includes = BaseActivityModule.class)
public abstract class CreateAccountActivityModule {
    @Binds
    @PerActivity
    abstract AppCompatActivity appCompatActivity(CreateAccountActivity createAccountActivity);

    @PerFragment
    @ContributesAndroidInjector(modules = AddAccountSourceFragmentModule.class)
    abstract AddAccountSourceFragment addAccountSourceFragmentInjector();

    @PerFragment
    @ContributesAndroidInjector(modules = ImportSeedFragmentModule.class)
    abstract ImportSeedFragment importSeedFragmentInjector();

    @PerFragment
    @ContributesAndroidInjector(modules = GenerateSeedFragmentModule.class)
    abstract GenerateSeedFragment generateSeedFragmentInjector();

    @PerFragment
    @ContributesAndroidInjector(modules = EncryptSeedFragmentModule.class)
    abstract EncryptSeedFragment encryptSeedFragmentInjector();
}
