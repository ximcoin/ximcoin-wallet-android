package com.ximcoin.ximwallet.model.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ximcoin.ximwallet.model.api.HorizonApi;
import com.ximcoin.ximwallet.model.fees.Fees;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class FeesRepository {
    @NonNull
    private final HorizonApi horizonApi;

    @Nullable
    private Fees fees;

    @Inject
    public FeesRepository(@NonNull HorizonApi horizonApi) {
        this.horizonApi = horizonApi;
    }

    @NonNull
    public Single<Fees> getFees() {
        if (fees != null) {
            return Single.just(fees);
        } else {
            return horizonApi.getFees()
                    .map(feesWrapper -> {
                       this.fees = feesWrapper.getFees();
                       return fees;
                    });
        }
    }
}
