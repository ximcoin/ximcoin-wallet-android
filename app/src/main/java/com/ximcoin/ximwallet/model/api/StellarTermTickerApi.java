package com.ximcoin.ximwallet.model.api;

import com.ximcoin.ximwallet.model.persistence.stellarterm.ConversionRate;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface StellarTermTickerApi {
    @GET("ticker.json")
    Single<ConversionRate> getConversionRate();
}
