package com.ximcoin.ximwallet.presenter.account;

import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.api.HorizonApi;
import com.ximcoin.ximwallet.model.repository.AccountRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class TrustXimPresenterModule {
    @Provides
    TrustXimContract.TrustXimPresenter provideTrustXimPresenter(TrustXimContract.TrustXimView view,
                                                                HorizonApi horizonApi,
                                                                AccountRepository accountRepository,
                                                                SchedulerProvider schedulerProvider) {
        return new TrustXimPresenter(view, horizonApi, accountRepository, schedulerProvider);
    }
}
