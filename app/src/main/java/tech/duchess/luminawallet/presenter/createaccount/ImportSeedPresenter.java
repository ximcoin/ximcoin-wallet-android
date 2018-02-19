package tech.duchess.luminawallet.presenter.createaccount;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import tech.duchess.luminawallet.model.util.AccountUtil;
import tech.duchess.luminawallet.presenter.common.BasePresenter;

public class ImportSeedPresenter extends BasePresenter<ImportSeedContract.ImportSeedView>
        implements ImportSeedContract.ImportSeedPresenter {

    ImportSeedPresenter(@NonNull ImportSeedContract.ImportSeedView view) {
        super(view);
    }

    @Override
    public void onUserImportSeed(@Nullable String seed) {
        if (!AccountUtil.secretSeedOfProperLength(seed)) {
            view.showError(ImportError.SEED_LENGTH_ERROR);
        } else if (!AccountUtil.secretSeedOfProperPrefix(seed)) {
            view.showError(ImportError.SEED_PREFIX_ERROR);
        } else if (!AccountUtil.secretSeedCanBeDecoded(seed)) {
            view.showError(ImportError.SEED_FORMAT_ERROR);
        } else {
            view.onSeedValidated(seed);
        }
    }

    @Override
    public void onSeedFieldContentsChanged() {
        view.clearError();
    }
}
