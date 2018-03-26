package com.ximcoin.ximwallet.view.contacts;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import com.ximcoin.ximwallet.dagger.module.BaseFragmentModule;
import com.ximcoin.ximwallet.dagger.scope.PerFragment;
import com.ximcoin.ximwallet.presenter.contacts.EditContactContract;
import com.ximcoin.ximwallet.presenter.contacts.EditContactPresenterModule;

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
