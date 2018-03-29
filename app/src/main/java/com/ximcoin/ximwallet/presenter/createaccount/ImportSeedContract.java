package com.ximcoin.ximwallet.presenter.createaccount;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ximcoin.ximwallet.presenter.common.Presenter;

public interface ImportSeedContract {
    interface ImportSeedView {
        void showError(@NonNull ImportSeedPresenter.ImportError error);
        void clearError();
        void onSeedValidated(@NonNull String seed);
        void showXimSetupLoading(boolean isLoading);
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
