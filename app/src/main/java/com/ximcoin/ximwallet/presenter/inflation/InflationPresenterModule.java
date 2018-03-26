package com.ximcoin.ximwallet.presenter.inflation;

import dagger.Module;
import dagger.Provides;
import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.api.HorizonApi;
import com.ximcoin.ximwallet.model.repository.AccountRepository;

@Module
public class InflationPresenterModule {
    @Provides
    InflationContract.InflationPresenter provideInflationPresenter(InflationContract.InflationView view,
                                                                   HorizonApi horizonApi,
                                                                   AccountRepository accountRepository,
                                                                   SchedulerProvider schedulerProvider) {
        return new InflationPresenter(view, horizonApi, accountRepository, schedulerProvider);
    }
}
