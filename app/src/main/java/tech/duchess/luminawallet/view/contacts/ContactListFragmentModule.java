package tech.duchess.luminawallet.view.contacts;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import tech.duchess.luminawallet.dagger.module.BaseFragmentModule;
import tech.duchess.luminawallet.dagger.scope.PerFragment;
import tech.duchess.luminawallet.presenter.contacts.ContactListContract;
import tech.duchess.luminawallet.presenter.contacts.ContactListPresenterModule;

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
