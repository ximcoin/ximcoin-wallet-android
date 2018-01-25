package tech.duchess.luminawallet.view.account.transactions;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.persistence.transaction.Operation;
import tech.duchess.luminawallet.presenter.account.transactions.TransactionsPresenter;
import tech.duchess.luminawallet.view.account.IAccountPerspectiveView;
import tech.duchess.luminawallet.view.util.ViewUtils;

// https://github.com/googlesamples/android-architecture-components/tree/master/PagingWithNetworkSample
public class TransactionsFragment extends Fragment implements IAccountPerspectiveView {
    private static final String ACCOUNT_KEY = "TransactionsFragment.ACCOUNT_KEY";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Inject
    TransactionsPresenter transactionsPresenter;

    @Nullable
    private String accountId;

    private LinearLayoutManager layoutManager;
    private Unbinder unbinder;
    private TransactionsAdapter adapter;
    private LiveData<PagedList<Operation>> liveData;

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
        View view = inflater.inflate(R.layout.recycler_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            ViewUtils.whenNonNull(getArguments(), args ->
                    ViewUtils.whenNonNull(args.getParcelable(ACCOUNT_KEY), account ->
                            accountId = ((Account) account).getAccount_id()));
        } else {
            accountId = savedInstanceState.getString(ACCOUNT_KEY);
        }

        initRecyclerView();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initRecyclerView() {
        Context context = getContext();
        if (context == null) {
            return;
        }

        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setItemAnimator(new SlideUpAnimator());
        adapter = new TransactionsAdapter(accountId);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        ViewUtils.addDividerDecoration(recyclerView, context, layoutManager.getOrientation());

        if (accountId != null) {
            liveData = transactionsPresenter.setAccountId(accountId);
            setDataListener();
        }
    }

    private void setDataListener() {
        if (liveData != null) {
            liveData.observe(this, operations -> adapter.setList(operations));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACCOUNT_KEY, accountId);
    }


    @Override
    public void setAccount(@Nullable Account account) {
        if (account == null) {
            accountId = null;
        } else {
            accountId = account.getAccount_id();
        }

        adapter.setAccountId(accountId);
        liveData = transactionsPresenter.setAccountId(accountId);
        setDataListener();
    }
}
