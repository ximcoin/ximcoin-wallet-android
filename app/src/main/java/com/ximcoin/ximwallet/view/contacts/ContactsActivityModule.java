package com.ximcoin.ximwallet.view.contacts;

import android.support.v7.app.AppCompatActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import com.ximcoin.ximwallet.dagger.module.BaseActivityModule;
import com.ximcoin.ximwallet.dagger.scope.PerActivity;
import com.ximcoin.ximwallet.dagger.scope.PerFragment;

@Module(includes = BaseActivityModule.class)
public abstract class ContactsActivityModule {
    @Binds
    @PerActivity
    abstract AppCompatActivity appCompatActivity(ContactsActivity contactsActivity);

    @PerFragment
    @ContributesAndroidInjector(modules = ContactListFragmentModule.class)
    abstract ContactListFragment contactListFragmentInjector();

    @PerFragment
    @ContributesAndroidInjector(modules = ViewContactFragmentModule.class)
    abstract ViewContactFragment viewContactFragmentInjector();

    @PerFragment
    @ContributesAndroidInjector(modules = EditContactFragmentModule.class)
    abstract EditContactFragment editContactFragmentInjector();
}
