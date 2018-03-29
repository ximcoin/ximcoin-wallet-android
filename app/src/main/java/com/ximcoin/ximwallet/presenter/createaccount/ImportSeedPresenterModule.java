package com.ximcoin.ximwallet.presenter.createaccount;

import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.api.HorizonApi;
import com.ximcoin.ximwallet.model.repository.FeesRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class ImportSeedPresenterModule {
    @Provides
    ImportSeedContract.ImportSeedPresenter provideImportSeedPresenter(ImportSeedContract.ImportSeedView importSeedView,
                                                                      HorizonApi horizonApi,
                                                                      FeesRepository feesRepository,
                                                                      SchedulerProvider schedulerProvider) {
        return new ImportSeedPresenter(importSeedView, horizonApi, feesRepository, schedulerProvider);
    }
}
