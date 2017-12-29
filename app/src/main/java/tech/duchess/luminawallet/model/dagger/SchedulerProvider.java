package tech.duchess.luminawallet.model.dagger;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.ObservableTransformer;

/**
 * Convenient way to inject a thread schedule for asynchronous work.
 */
@Singleton
public class SchedulerProvider {
    @Inject
    public SchedulerProvider() {}

    public <T> ObservableTransformer<T, T> appScheduler() {
        return new AppScheduler<>();
    }
}
