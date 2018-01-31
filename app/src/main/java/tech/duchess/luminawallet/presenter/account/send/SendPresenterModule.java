package tech.duchess.luminawallet.presenter.account.send;

import dagger.Module;
import dagger.Provides;
import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.api.HorizonApi;
import tech.duchess.luminawallet.model.repository.AccountRepository;

@Module
public class SendPresenterModule {
    @Provides
    SendContract.SendPresenter provideAccountsPresenter(SendContract.SendView view,
                                                        HorizonApi horizonApi,
                                                        AccountRepository accountRepository,
                                                        SchedulerProvider schedulerProvider) {
        return new SendPresenter(view, horizonApi, accountRepository, schedulerProvider);
    }
}
