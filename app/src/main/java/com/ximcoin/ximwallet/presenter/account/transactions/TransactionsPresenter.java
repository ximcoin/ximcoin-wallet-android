package com.ximcoin.ximwallet.presenter.account.transactions;

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
import com.ximcoin.ximwallet.model.api.HorizonApi;
import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.model.persistence.transaction.Operation;
import com.ximcoin.ximwallet.model.repository.OperationPageDataSource;
import com.ximcoin.ximwallet.presenter.account.transactions.TransactionsContract.NetworkState;
import com.ximcoin.ximwallet.presenter.common.BasePresenter;
import com.ximcoin.ximwallet.view.util.ViewUtils;

public class TransactionsPresenter extends BasePresenter<TransactionsContract.TransactionsView>
        implements TransactionsContract.TransactionsPresenter {
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
    private MutableLiveData<OperationPageDataSource> source = new MutableLiveData<>();

    private boolean hasBoundData = false;

    @Inject
    TransactionsPresenter(@NonNull TransactionsContract.TransactionsView view,
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
            // Do nothing, our view already has transactions loaded. The user can pull-to-refresh as
            // needed.
            return;
        } else {
            accountId = account.getAccount_id();
        }

        bindData();
    }

    @Override
    public void onTransactionPosted(@NonNull Account account) {
        if (account.getAccount_id().equals(accountId)) {
            // Current account we're showing has had a transaction post. Refresh transactions.
            onUserRefreshed();
        }
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
            OperationPageDataSource dataSource =
                    new OperationPageDataSource(horizonApi, okHttpClient, moshi, accountId);
            OperationPageDataSource oldSource = source.getValue();

            LiveData<NetworkState> oldNetworkState = null;
            LiveData<NetworkState> oldRefreshState = null;
            if (oldSource != null) {
                oldNetworkState = oldSource.getNetworkState();
                oldRefreshState = oldSource.getInitialLoadState();
            }

            view.observeRefreshState(oldRefreshState, dataSource.getInitialLoadState());
            view.observeNetworkState(oldNetworkState, dataSource.getNetworkState());
            source.postValue(dataSource);
            return dataSource;
        }
    }
}
