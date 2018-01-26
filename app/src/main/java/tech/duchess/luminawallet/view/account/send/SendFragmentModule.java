package tech.duchess.luminawallet.view.account.send;

import android.support.v4.app.Fragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import tech.duchess.luminawallet.dagger.module.BaseFragmentModule;
import tech.duchess.luminawallet.dagger.scope.PerFragment;
import tech.duchess.luminawallet.presenter.account.send.SendContract;
import tech.duchess.luminawallet.presenter.account.send.SendPresenterModule;

@Module(includes = {BaseFragmentModule.class, SendPresenterModule.class})
public abstract class SendFragmentModule {
    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(SendFragment sendFragment);

    @Binds
    @PerFragment
    abstract SendContract.SendView provideSendView(SendFragment sendFragment);
}
