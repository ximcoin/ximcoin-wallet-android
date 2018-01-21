package tech.duchess.luminawallet.presenter.createaccount;

import dagger.Module;
import dagger.Provides;
import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.persistence.HorizonDB;

@Module
public class EncryptSeedPresenterModule {
    @Provides
    EncryptSeedContract.EncryptSeedPresenter provideEncryptSeedPresenter(EncryptSeedContract.EncryptSeedView view,
                                                                         HorizonDB horizonDB,
                                                                         SchedulerProvider schedulerProvider) {
        return new EncryptSeedPresenter(view, horizonDB, schedulerProvider);
    }
}
