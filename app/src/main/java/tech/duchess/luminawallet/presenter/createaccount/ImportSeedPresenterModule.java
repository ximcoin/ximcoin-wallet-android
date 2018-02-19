package tech.duchess.luminawallet.presenter.createaccount;

import dagger.Module;
import dagger.Provides;

@Module
public class ImportSeedPresenterModule {
    @Provides
    ImportSeedContract.ImportSeedPresenter provideImportSeedPresenter(ImportSeedContract.ImportSeedView importSeedView) {
        return new ImportSeedPresenter(importSeedView);
    }
}
