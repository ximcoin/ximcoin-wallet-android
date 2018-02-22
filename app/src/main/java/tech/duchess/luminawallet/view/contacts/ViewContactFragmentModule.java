package tech.duchess.luminawallet.view.contacts;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import tech.duchess.luminawallet.dagger.module.BaseFragmentModule;
import tech.duchess.luminawallet.dagger.scope.PerFragment;
import tech.duchess.luminawallet.presenter.contacts.ViewContactContract;
import tech.duchess.luminawallet.presenter.contacts.ViewContactPresenterModule;

@Module(includes = {BaseFragmentModule.class, ViewContactPresenterModule.class})
abstract class ViewContactFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(ViewContactFragment viewContactFragment);

    @Binds
    @PerFragment
    abstract ViewContactContract.ViewContactView provideViewContactView(ViewContactFragment viewContactFragment);
}
