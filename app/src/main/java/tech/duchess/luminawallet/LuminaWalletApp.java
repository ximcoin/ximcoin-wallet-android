package tech.duchess.luminawallet;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.moshi.Moshi;

import javax.inject.Inject;

import tech.duchess.luminawallet.model.dagger.component.AppComponent;
import tech.duchess.luminawallet.model.dagger.component.DaggerAppComponent;
import tech.duchess.luminawallet.model.dagger.module.AppModule;

/**
 * Base class for maintaining global application state.
 */
public class LuminaWalletApp extends Application {

    private AppComponent appComponent;

    @Inject
    Moshi moshi;

    @Nullable
    private static LuminaWalletApp application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        appComponent.inject(this);
    }

    @NonNull
    public static LuminaWalletApp getInstance() {
        if (application == null) {
            application = new LuminaWalletApp();
        }

        return application;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public Moshi getMoshi() {
        return moshi;
    }
}
