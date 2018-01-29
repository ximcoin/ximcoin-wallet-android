package tech.duchess.luminawallet.view.account.transactions;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import dagger.android.support.AndroidSupportInjection;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.persistence.transaction.Operation;
import tech.duchess.luminawallet.presenter.account.transactions.TransactionsContract;
import tech.duchess.luminawallet.view.account.IAccountPerspectiveView;
import tech.duchess.luminawallet.view.common.BaseViewFragment;
import tech.duchess.luminawallet.view.util.ViewUtils;

// https://github.com/googlesamples/android-architecture-components/tree/master/PagingWithNetworkSample
public class TransactionsFragment extends BaseViewFragment<TransactionsContract.TransactionsPresenter>
        implements IAccountPerspectiveView, TransactionsContract.TransactionsView {
    private static final String ACCOUNT_KEY = "TransactionsFragment.ACCOUNT_KEY";

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_fragment, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        initRecyclerView();
        if (savedInstanceState == null) {
            ViewUtils.whenNonNull(getArguments(), args ->
                    ViewUtils.whenNonNull(args.getParcelable(ACCOUNT_KEY), account -> {
                        presenter.onAccountSet((Account) account);
                    }));
        }
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
    public void observeData(@Nullable LiveData<PagedList<Operation>> oldData,
                            @NonNull LiveData<PagedList<Operation>> liveData,
                            @Nullable String accountId) {
        ViewUtils.whenNonNull(oldData, old -> old.removeObservers(this));

        adapter.setAccountId(accountId);
        liveData.observe(this, operations -> {
            adapter.setList(operations);
        });
    }
}
