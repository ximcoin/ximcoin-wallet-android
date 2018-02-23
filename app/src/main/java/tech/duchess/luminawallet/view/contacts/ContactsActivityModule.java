package tech.duchess.luminawallet.view.contacts;

import android.support.v7.app.AppCompatActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import tech.duchess.luminawallet.dagger.module.BaseActivityModule;
import tech.duchess.luminawallet.dagger.scope.PerActivity;
import tech.duchess.luminawallet.dagger.scope.PerFragment;

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
