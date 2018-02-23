package tech.duchess.luminawallet.view.contacts;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import tech.duchess.luminawallet.dagger.module.BaseFragmentModule;
import tech.duchess.luminawallet.dagger.scope.PerFragment;
import tech.duchess.luminawallet.presenter.contacts.EditContactContract;
import tech.duchess.luminawallet.presenter.contacts.EditContactPresenterModule;

@Module(includes = {BaseFragmentModule.class, EditContactPresenterModule.class})
abstract class EditContactFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(EditContactFragment editContactFragment);

    @Binds
    @PerFragment
    abstract EditContactContract.EditContactView provideEditContactView(EditContactFragment editContactFragment);
}
