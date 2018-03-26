package com.ximcoin.ximwallet.view.contacts;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import com.ximcoin.ximwallet.dagger.module.BaseFragmentModule;
import com.ximcoin.ximwallet.dagger.scope.PerFragment;
import com.ximcoin.ximwallet.presenter.contacts.ContactListContract;
import com.ximcoin.ximwallet.presenter.contacts.ContactListPresenterModule;

@Module(includes = {BaseFragmentModule.class, ContactListPresenterModule.class})
abstract class ContactListFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(ContactListFragment contactListFragment);

    @Binds
    @PerFragment
    abstract ContactListContract.ContactListView provideContactListView(ContactListFragment contactListFragment);
}
