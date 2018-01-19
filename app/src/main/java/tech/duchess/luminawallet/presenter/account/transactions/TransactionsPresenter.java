package tech.duchess.luminawallet.presenter.account.transactions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

import javax.inject.Inject;

import tech.duchess.luminawallet.model.api.HorizonApi;
import tech.duchess.luminawallet.model.dagger.SchedulerProvider;
import timber.log.Timber;

public class TransactionsPresenter {
    private final static int PAGE_SIZE = 10;

    @NonNull
    private final HorizonApi horizonApi;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final LifecycleProvider<FragmentEvent> lifecycleProvider;

    @Inject
    public TransactionsPresenter(@NonNull HorizonApi horizonApi,
                                 @NonNull SchedulerProvider schedulerProvider,
                                 @NonNull LifecycleProvider<FragmentEvent> lifecycleProvider) {
        this.horizonApi = horizonApi;
        this.schedulerProvider = schedulerProvider;
        this.lifecycleProvider = lifecycleProvider;
    }

    public void test(@Nullable String accountId) {
        if (accountId != null) {
            horizonApi.getFirstOperationsPage(accountId, PAGE_SIZE)
                    .compose(schedulerProvider.singleScheduler())
                    .compose(lifecycleProvider.bindUntilEvent(FragmentEvent.DESTROY))
                    .subscribe(operationPage -> {
                        Timber.d("Operation load success");
                    }, throwable -> {
                        Timber.e(throwable, "Failed to load operations");
                    });
        }
    }
}
