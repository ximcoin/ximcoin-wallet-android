package com.ximcoin.ximwallet.model.api;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import com.ximcoin.ximwallet.model.fees.FeesWrapper;
import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.model.persistence.transaction.OperationPage;
import com.ximcoin.ximwallet.model.persistence.transaction.Transaction;

/**
 * Represents the Horizon RESTful API.
 */
public interface HorizonApi {
    @GET("accounts/{accountId}")
    Single<Account> getAccount(@Path("accountId") String accountId);

    @GET("ledgers?limit=1&order=desc")
    Single<FeesWrapper> getFees();

    @GET("accounts/{accountId}/operations?order=desc")
    Single<OperationPage> getFirstOperationsPage(@Path("accountId") String accountId,
                                                 @Query("limit") int pageSize);

    @GET("transactions/{transactionHash}")
    Single<Transaction> getTransaction(@Path("transactionHash") String transactionHash);

    @FormUrlEncoded
    @POST("transactions")
    Completable postTransaction(@Field("tx") String transactionEnvelopeXDRBase64);
}
