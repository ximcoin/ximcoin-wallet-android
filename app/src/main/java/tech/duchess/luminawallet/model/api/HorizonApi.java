package tech.duchess.luminawallet.model.api;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import tech.duchess.luminawallet.model.fees.Fees;
import tech.duchess.luminawallet.model.persistence.account.Account;

/**
 * Represents the Horizon RESTful API.
 */
public interface HorizonApi {
    @GET("accounts/{accountId}")
    Single<Account> getAccount(@Path("accountId") String accountId);

    @GET("ledgers/{sequence}")
    Single<Fees> getFees(@Path("sequence") String sequence);
}
