package com.ximcoin.ximwallet.presenter.account.transactions;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.model.persistence.transaction.Operation;
import com.ximcoin.ximwallet.presenter.common.Presenter;

// I know I'm really botching the whole aspect of this live data thing... but for now this works.
// I really only wanted to leverage it for the ease of paging data, not the databinding or
// lifecycle-awareness per se.
public interface TransactionsContract {
    interface TransactionsView {
        void observeData(@Nullable LiveData<PagedList<Operation>> oldData,
                         @NonNull LiveData<PagedList<Operation>> liveData,
                         @Nullable String accountId);

        void observeNetworkState(@Nullable LiveData<NetworkState> oldData,
                                 @NonNull LiveData<NetworkState> newData);

        void observeRefreshState(@Nullable LiveData<NetworkState> oldData,
                                 @NonNull LiveData<NetworkState> newData);
    }

    interface TransactionsPresenter extends Presenter {
        void onAccountSet(@Nullable Account account);

        void onTransactionPosted(@NonNull Account account);

        void onUserRefreshed();
    }

    enum NetworkState {
        LOADING,
        SUCCESS,
        FAILED
    }
}
