package tech.duchess.luminawallet.model.dagger.module;

import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Allows for a convenient way to inject the proper {@link LifecycleProvider} to respective
 * presenters.
 */
@Module
public class FragmentLifecycleModule {
    @NonNull
    private final RxFragment rxFragment;

    public FragmentLifecycleModule(@NonNull RxFragment rxFragment) {
        this.rxFragment = rxFragment;
    }

    @Provides
    public LifecycleProvider<FragmentEvent> provideLifecycleProvider() {
        return rxFragment;
    }
}
