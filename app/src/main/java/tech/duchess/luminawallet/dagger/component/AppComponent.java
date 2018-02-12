package tech.duchess.luminawallet.dagger.component;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import tech.duchess.luminawallet.LuminaWalletApp;
import tech.duchess.luminawallet.dagger.module.AppModule;
import tech.duchess.luminawallet.view.nav.NavHeaderViewModule;

// https://github.com/vestrel00/android-dagger-butterknife-mvp/tree/master-support
// https://proandroiddev.com/implementing-mvp-with-new-dagger-android-injection-api-773b13e1ef0
@Singleton
@Component(modules = {AndroidInjectionModule.class, AppModule.class})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    void inject(LuminaWalletApp app);

    NavHeaderComponent plus(NavHeaderViewModule module);
}
