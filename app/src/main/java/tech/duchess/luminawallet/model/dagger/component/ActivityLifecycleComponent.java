package tech.duchess.luminawallet.model.dagger.component;

import dagger.Subcomponent;
import tech.duchess.luminawallet.model.dagger.module.ActivityLifecycleModule;
import tech.duchess.luminawallet.view.account.AccountsActivity;

@Subcomponent(modules = ActivityLifecycleModule.class)
public interface ActivityLifecycleComponent {
    void inject(AccountsActivity accountsActivity);
}
