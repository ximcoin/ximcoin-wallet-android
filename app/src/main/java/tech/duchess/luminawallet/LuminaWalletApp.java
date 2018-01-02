package tech.duchess.luminawallet;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.moshi.Moshi;

import javax.inject.Inject;

import tech.duchess.luminawallet.model.dagger.component.AppComponent;
import tech.duchess.luminawallet.model.dagger.component.DaggerAppComponent;
import tech.duchess.luminawallet.model.dagger.module.AppModule;
import timber.log.Timber;

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

        if (EnvironmentConstants.IS_PRODUCTION) {
            Timber.plant(new CrashReportingTree());
        } else {
            Timber.plant(new Timber.DebugTree());
        }
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

    private class CrashReportingTree extends Timber.Tree {

        @Override
        protected void log(int priority,
                           @Nullable String tag,
                           @NonNull String message,
                           @Nullable Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            // TODO: Production crash reporting
            /*FakeCrashLibrary.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    FakeCrashLibrary.logError(t);
                } else if (priority == Log.WARN) {
                    FakeCrashLibrary.logWarning(t);
                }
            }*/
        }
    }
}
