package tech.duchess.luminawallet.presenter.createaccount;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import tech.duchess.luminawallet.presenter.common.Presenter;

public interface ImportSeedContract {
    interface ImportSeedView {
        void showError(@NonNull ImportSeedPresenter.ImportError error);
        void clearError();
        void onSeedValidated(@NonNull String seed);
    }

    interface ImportSeedPresenter extends Presenter {
        enum ImportError {
            SEED_LENGTH_ERROR,
            SEED_PREFIX_ERROR,
            SEED_FORMAT_ERROR
        }

        void onUserImportSeed(@Nullable String seed);

        void onSeedFieldContentsChanged();
    }
}
