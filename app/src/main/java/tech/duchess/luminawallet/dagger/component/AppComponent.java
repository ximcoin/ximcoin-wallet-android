package tech.duchess.luminawallet.dagger.component;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import tech.duchess.luminawallet.LuminaWalletApp;
import tech.duchess.luminawallet.dagger.module.AppModule;

// https://github.com/vestrel00/android-dagger-butterknife-mvp/tree/master-support
// https://proandroiddev.com/implementing-mvp-with-new-dagger-android-injection-api-773b13e1ef0
@Singleton
@Component(modules = AppModule.class)
interface AppComponent extends AndroidInjector<LuminaWalletApp> {
    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<LuminaWalletApp> {
    }
}
