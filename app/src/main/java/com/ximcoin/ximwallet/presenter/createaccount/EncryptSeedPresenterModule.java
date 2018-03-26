package com.ximcoin.ximwallet.presenter.createaccount;

import dagger.Module;
import dagger.Provides;
import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.persistence.HorizonDB;

@Module
public class EncryptSeedPresenterModule {
    @Provides
    EncryptSeedContract.EncryptSeedPresenter provideEncryptSeedPresenter(EncryptSeedContract.EncryptSeedView view,
                                                                         HorizonDB horizonDB,
                                                                         SchedulerProvider schedulerProvider) {
        return new EncryptSeedPresenter(view, horizonDB, schedulerProvider);
    }
}
