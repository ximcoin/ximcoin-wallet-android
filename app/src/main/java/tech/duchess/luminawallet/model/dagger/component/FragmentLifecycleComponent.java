package tech.duchess.luminawallet.model.dagger.component;

import dagger.Subcomponent;
import tech.duchess.luminawallet.model.dagger.module.FragmentLifecycleModule;
import tech.duchess.luminawallet.view.createaccount.EncryptSeedFragment;

@Subcomponent(modules = FragmentLifecycleModule.class)
public interface FragmentLifecycleComponent {
    void inject(EncryptSeedFragment fragment);
}
