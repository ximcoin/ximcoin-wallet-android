package tech.duchess.luminawallet.presenter.account.transactions;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.moshi.Moshi;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import tech.duchess.luminawallet.model.api.HorizonApi;
import tech.duchess.luminawallet.model.persistence.transaction.Operation;
import tech.duchess.luminawallet.model.repository.OperationPageDataSource;

public class TransactionsPresenter {
    private final static int PAGE_SIZE = 7;

    @NonNull
    private final HorizonApi horizonApi;

    @NonNull
    private final OkHttpClient okHttpClient;

    @NonNull
    private final Moshi moshi;

    @Inject
    public TransactionsPresenter(@NonNull HorizonApi horizonApi,
                                 @NonNull OkHttpClient okHttpClient,
                                 @NonNull Moshi moshi) {
        this.horizonApi = horizonApi;
        this.okHttpClient = okHttpClient;
        this.moshi = moshi;
    }

    public LiveData<PagedList<Operation>> setAccountId(@Nullable String accountId) {
        return new LivePagedListBuilder<>(new OperationsDataSourceFactory(accountId), PAGE_SIZE)
                .build();
    }

    private class OperationsDataSourceFactory implements DataSource.Factory<String, Operation> {
        @Nullable
        private final String accountId;

        public OperationsDataSourceFactory(@Nullable String accountId) {
            this.accountId = accountId;
        }

        @Override
        public DataSource<String, Operation> create() {
            return new OperationPageDataSource(horizonApi, okHttpClient, moshi, accountId);
        }
    }
}
