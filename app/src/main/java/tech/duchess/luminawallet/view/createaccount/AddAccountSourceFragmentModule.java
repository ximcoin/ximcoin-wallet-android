package tech.duchess.luminawallet.view.createaccount;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import tech.duchess.luminawallet.dagger.module.BaseFragmentModule;
import tech.duchess.luminawallet.dagger.scope.PerFragment;

@Module(includes = BaseFragmentModule.class)
public abstract class AddAccountSourceFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(AddAccountSourceFragment addAccountSourceFragment);
}
