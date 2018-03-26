package com.ximcoin.ximwallet.model.api;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import com.ximcoin.ximwallet.model.persistence.coinmarketcap.ConversionRate;

public interface CoinMarketCapApi {
    @GET("ticker/stellar/")
    Single<ConversionRate> getConversionRate(@Query("convert") String currency);
}
