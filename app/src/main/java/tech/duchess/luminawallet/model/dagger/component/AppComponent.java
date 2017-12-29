package tech.duchess.luminawallet.model.dagger.component;

import javax.inject.Singleton;

import dagger.Component;
import tech.duchess.luminawallet.model.dagger.module.ActivityLifecycleModule;
import tech.duchess.luminawallet.model.dagger.module.AppModule;
import tech.duchess.luminawallet.model.dagger.module.FragmentLifecycleModule;
import tech.duchess.luminawallet.model.dagger.module.HorizonApiModule;

@Singleton
@Component(modules = {AppModule.class, HorizonApiModule.class})
public interface AppComponent {
    FragmentLifecycleComponent plus(FragmentLifecycleModule fragmentLifecycleModule);
    ActivityLifecycleComponent plus(ActivityLifecycleModule activityLifecycleModule);
}
