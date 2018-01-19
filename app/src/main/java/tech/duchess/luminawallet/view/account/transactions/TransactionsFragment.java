package tech.duchess.luminawallet.view.account.transactions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tech.duchess.luminawallet.LuminaWalletApp;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.dagger.module.FragmentLifecycleModule;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.presenter.account.transactions.TransactionsPresenter;
import tech.duchess.luminawallet.view.account.IAccountPerspectiveView;
import tech.duchess.luminawallet.view.util.ViewBindingUtils;

// https://medium.com/@etiennelawlor/pagination-with-recyclerview-1cb7e66a502b
// https://medium.com/fueled-android/scroll-pull-bind-paginate-recyclerview-30d5aed8b43a
public class TransactionsFragment extends RxFragment implements IAccountPerspectiveView {
    private static final String ACCOUNT_KEY = "TransactionsFragment.ACCOUNT_KEY";
    private static final int PAGE_SIZE = 20;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Inject
    TransactionsPresenter transactionsPresenter;

    @Nullable
    private String accountId;

    private TransactionRecyclerAdapter adapter;
    private LinearLayoutManager layoutManager;
    private Unbinder unbinder;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;

    public static TransactionsFragment newInstance(@Nullable Account account) {
        Bundle args = new Bundle();
        args.putParcelable(ACCOUNT_KEY, account);
        TransactionsFragment fragment = new TransactionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LuminaWalletApp.getInstance()
                .getAppComponent()
                .plus(new FragmentLifecycleModule(this))
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initRecyclerView();

        if (savedInstanceState == null) {
            ViewBindingUtils.whenNonNull(getArguments(), args ->
                    ViewBindingUtils.whenNonNull(args.getParcelable(ACCOUNT_KEY), account ->
                            accountId = ((Account) account).getAccount_id()));
            loadFirstItems();
        } else {
            accountId = savedInstanceState.getString(accountId);
        }

        return view;
    }

    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setItemAnimator(new SlideUpAnimator());
        adapter = new TransactionRecyclerAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new ScrollListener());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACCOUNT_KEY, accountId);
    }

    private void loadFirstItems() {
        transactionsPresenter.test(accountId);
    }

    private void loadMoreItems() {
        isLoading = true;
        currentPage++;

        // findMoreTransactionsCall
    }

    @Override
    public void setAccount(@Nullable Account account) {
        if (account == null) {
            accountId = null;
        } else {
            accountId = account.getAccount_id();
        }
    }

    private class TransactionRecyclerAdapter extends RecyclerView.Adapter<TransactionViewHolder> {

        @Override
        public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TransactionViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.balance_recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(TransactionViewHolder holder, int position) {
            holder.bindData();
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {

        TransactionViewHolder(View itemView) {
            super(itemView);
        }

        void bindData() {

        }
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {


        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!isLoading && !isLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    loadMoreItems();
                }
            }
        }
    }
}
