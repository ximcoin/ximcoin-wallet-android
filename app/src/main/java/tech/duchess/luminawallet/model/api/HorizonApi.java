package tech.duchess.luminawallet.model.api;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tech.duchess.luminawallet.model.fees.Fees;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.persistence.transaction.OperationPage;
import tech.duchess.luminawallet.model.persistence.transaction.Transaction;

/**
 * Represents the Horizon RESTful API.
 */
public interface HorizonApi {
    @GET("accounts/{accountId}")
    Single<Account> getAccount(@Path("accountId") String accountId);

    @GET("ledgers/{sequence}")
    Single<Fees> getFees(@Path("sequence") String sequence);

    @GET("accounts/{accountId}/operations?order=desc")
    Single<OperationPage> getFirstOperationsPage(@Path("accountId") String accountId,
                                                 @Query("limit") int pageSize);

    @GET("transactions/{transactionHash}")
    Single<Transaction> getTransaction(@Path("transactionHash") String transactionHash);
}
