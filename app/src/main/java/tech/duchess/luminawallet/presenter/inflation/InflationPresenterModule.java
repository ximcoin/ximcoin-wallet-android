package tech.duchess.luminawallet.presenter.inflation;

import dagger.Module;
import dagger.Provides;
import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.api.HorizonApi;
import tech.duchess.luminawallet.model.repository.AccountRepository;

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
