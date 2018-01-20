package tech.duchess.luminawallet.model.repository;

import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tech.duchess.luminawallet.model.api.HorizonApi;
import tech.duchess.luminawallet.model.persistence.transaction.Operation;
import tech.duchess.luminawallet.model.persistence.transaction.OperationPage;
import timber.log.Timber;

public class OperationPageDataSource extends PageKeyedDataSource<String, Operation> {
    @NonNull
    private final HorizonApi horizonApi;

    @NonNull
    private final OkHttpClient okHttpClient;

    @NonNull
    private final String accountId;

    @NonNull
    private final JsonAdapter<OperationPage> operationPageAdapter;

    public OperationPageDataSource(@NonNull HorizonApi horizonApi,
                                   @NonNull OkHttpClient okHttpClient,
                                   @NonNull Moshi moshi,
                                   @NonNull String accountId) {
        this.horizonApi = horizonApi;
        this.okHttpClient = okHttpClient;
        this.accountId = accountId;
        operationPageAdapter = moshi.adapter(OperationPage.class);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params,
                            @NonNull LoadInitialCallback<String, Operation> callback) {
        OperationPage operationPage =
                horizonApi.getFirstOperationsPage(accountId, params.requestedLoadSize).blockingGet();
        callback.onResult(operationPage.getOperations(),
                operationPage.getPreviousPageLink(),
                operationPage.getNextPageLink());
    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params,
                           @NonNull LoadCallback<String, Operation> callback) {
        // Ignored, since we only ever append to our initial load.
    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params,
                          @NonNull LoadCallback<String, Operation> callback) {
        Request request = new Request.Builder().url(params.key).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            OperationPage operationPage = operationPageAdapter.fromJson(response.body().source());
            callback.onResult(operationPage.getOperations(), operationPage.getNextPageLink());
        } catch (IOException e) {
            Timber.e(e, "Failed to load more operations");
        }
    }
}
