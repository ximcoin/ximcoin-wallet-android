package com.ximcoin.ximwallet.view.account.transactions;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import com.ximcoin.ximwallet.R;
import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.model.persistence.transaction.Operation;
import com.ximcoin.ximwallet.presenter.account.transactions.TransactionsContract;
import com.ximcoin.ximwallet.presenter.account.transactions.TransactionsContract.NetworkState;
import com.ximcoin.ximwallet.view.account.AccountPerspectiveView;
import com.ximcoin.ximwallet.view.common.BaseViewFragment;
import com.ximcoin.ximwallet.view.util.ViewUtils;

// https://github.com/googlesamples/android-architecture-components/tree/master/PagingWithNetworkSample
public class TransactionsFragment extends BaseViewFragment<TransactionsContract.TransactionsPresenter>
        implements AccountPerspectiveView, TransactionsContract.TransactionsView {
    private static final String ACCOUNT_KEY = "TransactionsFragment.ACCOUNT_KEY";

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private TransactionsAdapter adapter;

    public static TransactionsFragment newInstance(@Nullable Account account) {
        Bundle args = new Bundle();
        args.putParcelable(ACCOUNT_KEY, account);
        TransactionsFragment fragment = new TransactionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.transactions_fragment, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        initRecyclerView();
        if (savedInstanceState == null) {
            ViewUtils.whenNonNull(getArguments(), args ->
                    ViewUtils.whenNonNull(args.getParcelable(ACCOUNT_KEY), account ->
                            presenter.onAccountSet((Account) account)));
        }
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccentDark,
                R.color.colorAccentLight);
        swipeRefreshLayout.setOnRefreshListener(() -> presenter.onUserRefreshed());
    }

    private void initRecyclerView() {
        Context context = getContext();
        if (context == null) {
            return;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setItemAnimator(new SlideUpAnimator());
        adapter = new TransactionsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        ViewUtils.addDividerDecoration(recyclerView, context, layoutManager.getOrientation());
    }


    @Override
    public void setAccount(@Nullable Account account) {
        presenter.onAccountSet(account);
    }

    @Override
    public void transactionPostedForAccount(@NonNull Account account) {
        presenter.onTransactionPosted(account);
    }

    @Override
    public void observeData(@Nullable LiveData<PagedList<Operation>> oldData,
                            @NonNull LiveData<PagedList<Operation>> liveData,
                            @Nullable String accountId) {
        removeObservers(oldData);
        adapter.setAccountId(accountId);
        liveData.observe(this, operations -> adapter.setList(operations));
    }

    @Override
    public void observeNetworkState(@Nullable LiveData<NetworkState> oldData,
                                    @NonNull LiveData<NetworkState> newData) {
        removeObservers(oldData);
        newData.observe(this, networkState -> {
            adapter.setLoading(networkState == NetworkState.LOADING);
            if (networkState == NetworkState.FAILED) {
                onLoadFailed();
            }
        });
    }

    @Override
    public void observeRefreshState(@Nullable LiveData<NetworkState> oldData,
                                    @NonNull LiveData<NetworkState> newData) {
        removeObservers(oldData);
        newData.observe(this, networkState -> {
            swipeRefreshLayout.setRefreshing(networkState == NetworkState.LOADING);
            if (networkState == NetworkState.FAILED) {
                onLoadFailed();
            }
        });
    }

    private void onLoadFailed() {
        Toast.makeText(getContext(), R.string.failed_load_transactions,
                Toast.LENGTH_SHORT).show();
    }

    private void removeObservers(@Nullable LiveData oldData) {
        new Handler(Looper.getMainLooper()).post(() ->
                ViewUtils.whenNonNull(oldData, old ->
                        old.removeObservers(TransactionsFragment.this)));
    }
}
