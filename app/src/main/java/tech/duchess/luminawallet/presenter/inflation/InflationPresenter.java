package tech.duchess.luminawallet.presenter.inflation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.api.HorizonApi;
import tech.duchess.luminawallet.model.repository.AccountRepository;
import tech.duchess.luminawallet.presenter.common.BasePresenter;

public class InflationPresenter extends BasePresenter<InflationContract.InflationView>
        implements InflationContract.InflationPresenter {
    @NonNull
    private final HorizonApi horizonApi;

    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    InflationPresenter(@NonNull InflationContract.InflationView view,
                       @NonNull HorizonApi horizonApi,
                       @NonNull AccountRepository accountRepository,
                       @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.horizonApi = horizonApi;
        this.schedulerProvider = schedulerProvider;
        this.accountRepository = accountRepository;
    }

    @Override
    public void start(@Nullable Bundle bundle) {
        super.start(bundle);
    }

    @Override
    public void onUserSetInflationDestination(@Nullable String destination) {

    }

    @Override
    public void onUserConfirmedInflationRemoval() {

    }

    @Override
    public void onUserConfirmedOperation(@Nullable String password) {

    }
}
