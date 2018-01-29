package tech.duchess.luminawallet.presenter.account.transactions;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.moshi.Moshi;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import tech.duchess.luminawallet.model.api.HorizonApi;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.persistence.transaction.Operation;
import tech.duchess.luminawallet.model.repository.OperationPageDataSource;
import tech.duchess.luminawallet.presenter.common.BasePresenter;
import tech.duchess.luminawallet.view.util.ViewUtils;

public class TransactionsPresenter extends BasePresenter<TransactionsContract.TransactionsView>
        implements TransactionsContract.TransactionsPresenter{
    private static final String ACCOUNT_ID_KEY = "TransactionsPresenter.ACCOUNT_ID_KEY";
    private final static int PAGE_SIZE = 7;

    @NonNull
    private final HorizonApi horizonApi;

    @NonNull
    private final OkHttpClient okHttpClient;

    @NonNull
    private final Moshi moshi;

    @Nullable
    private String accountId;

    @Nullable
    private LiveData<PagedList<Operation>> liveData;

    @NonNull
    private MutableLiveData<DataSource<String, Operation>> source = new MutableLiveData<>();

    private boolean hasBoundData = false;

    @Inject
    public TransactionsPresenter(@NonNull TransactionsContract.TransactionsView view,
                                 @NonNull HorizonApi horizonApi,
                                 @NonNull OkHttpClient okHttpClient,
                                 @NonNull Moshi moshi) {
        super(view);
        this.horizonApi = horizonApi;
        this.okHttpClient = okHttpClient;
        this.moshi = moshi;
    }

    @Override
    public void onAccountSet(@Nullable Account account) {
        if (account == null || !account.isOnNetwork()) {
            accountId = null;
        } else if (accountId != null && accountId.equals(account.getAccount_id())) {
            onUserRefreshed();
            return;
        } else {
            accountId = account.getAccount_id();
        }

        bindData();
    }

    @Override
    public void onUserRefreshed() {
        ViewUtils.whenNonNull(source.getValue(), DataSource::invalidate);
    }

    private void bindData() {
        LiveData<PagedList<Operation>> newData = getData();
        view.observeData(liveData, newData, accountId);
        liveData = newData;
    }

    private LiveData<PagedList<Operation>> getData() {
        return new LivePagedListBuilder<>(new OperationsDataSourceFactory(accountId), PAGE_SIZE)
                .build();
    }

    @Override
    public void saveState(@Nullable Bundle bundle) {
        super.saveState(bundle);
        ViewUtils.whenNonNull(bundle, b -> b.putString(ACCOUNT_ID_KEY, accountId));
    }

    @Override
    public void start(@Nullable Bundle bundle) {
        super.start(bundle);
        ViewUtils.whenNonNull(bundle, b -> accountId = b.getString(ACCOUNT_ID_KEY));
    }

    @Override
    public void resume() {
        super.resume();
        if (!hasBoundData) {
            // Don't rebind data across resumes (may happen multiple times across the lifetime of
            // this presenter). Rebind for first resume or new instances.
            hasBoundData = true;
            bindData();
        }
    }

    private class OperationsDataSourceFactory implements DataSource.Factory<String, Operation> {
        @Nullable
        private final String accountId;

        OperationsDataSourceFactory(@Nullable String accountId) {
            this.accountId = accountId;
        }

        @Override
        public DataSource<String, Operation> create() {
            DataSource<String, Operation> dataSource =
                    new OperationPageDataSource(horizonApi, okHttpClient, moshi, accountId);
            source.postValue(dataSource);
            return dataSource;
        }
    }
}
