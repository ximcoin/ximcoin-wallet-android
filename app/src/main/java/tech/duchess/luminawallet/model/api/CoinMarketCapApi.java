package tech.duchess.luminawallet.model.api;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tech.duchess.luminawallet.model.persistence.coinmarketcap.ConversionRate;

public interface CoinMarketCapApi {
    @GET("ticker/stellar/")
    Single<ConversionRate> getConversionRate(@Query("convert") String currency);
}
