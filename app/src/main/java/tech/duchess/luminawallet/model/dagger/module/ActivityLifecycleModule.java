package tech.duchess.luminawallet.model.dagger.module;

import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Allows for a convenient way to inject the proper {@link LifecycleProvider} to respective
 * presenters.
 */
@Module
public class ActivityLifecycleModule {
    @NonNull
    private final RxAppCompatActivity rxAppCompatActivity;

    public ActivityLifecycleModule(@NonNull RxAppCompatActivity rxAppCompatActivity) {
        this.rxAppCompatActivity = rxAppCompatActivity;
    }

    @Provides
    public LifecycleProvider<ActivityEvent> provideLifecycleProvider() {
        return rxAppCompatActivity;
    }
}
