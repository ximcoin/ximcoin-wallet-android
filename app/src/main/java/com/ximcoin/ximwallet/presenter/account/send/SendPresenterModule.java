package com.ximcoin.ximwallet.presenter.account.send;

import dagger.Module;
import dagger.Provides;
import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.api.HorizonApi;
import com.ximcoin.ximwallet.model.repository.AccountRepository;

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
