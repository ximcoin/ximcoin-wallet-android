package tech.duchess.luminawallet.model.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import tech.duchess.luminawallet.model.Account;

/**
 * Represents the Horizon RESTful API.
 */
public interface HorizonApi {
    @GET("accounts/{accountId}")
    Observable<Account> getAccount(@Path("accountId") String accountId);
}
