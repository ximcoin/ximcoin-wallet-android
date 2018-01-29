package tech.duchess.luminawallet.view.account.balance;

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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.persistence.account.Balance;
import tech.duchess.luminawallet.view.account.IAccountPerspectiveView;
import tech.duchess.luminawallet.view.util.ViewUtils;

public class BalancesFragment extends Fragment implements IAccountPerspectiveView {
    private static final String ACCOUNT_KEY = "BalancesFragment.ACCOUNT_KEY";
    private static final String BALANCES_KEY = "BalancesFragment.BALANCES_KEY";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private BalanceRecyclerAdapter adapter;
    private Unbinder unbinder;

    public static BalancesFragment newInstance(@Nullable Account account) {
        Bundle args = new Bundle();
        args.putParcelable(ACCOUNT_KEY, account);
        BalancesFragment fragment = new BalancesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        adapter = new BalanceRecyclerAdapter();

        if (savedInstanceState == null) {
            ViewUtils.whenNonNull(getArguments(), args ->
                    ViewUtils.whenNonNull(args.getParcelable(ACCOUNT_KEY), account ->
                            adapter.setBalances(((Account) account).getBalances())));
        } else {
            adapter.restoreState(savedInstanceState);
        }

        initRecycler();

        return view;
    }

    private void initRecycler() {
        Context context = getContext();
        if (context == null) {
            return;
        }

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        ViewUtils.addDividerDecoration(recyclerView, context, layoutManager.getOrientation());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setAccount(@Nullable Account account) {
        adapter.setBalances(account == null ? new ArrayList<>() : account.getBalances());
    }

    private class BalanceRecyclerAdapter extends RecyclerView.Adapter<BalanceViewHolder> {
        private final ArrayList<Balance> balances = new ArrayList<>();

        @Override
        public BalanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BalanceViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.balance_recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(BalanceViewHolder holder, int position) {
            holder.bindData(balances.get(position));
        }

        @Override
        public int getItemCount() {
            return balances.size();
        }

        void setBalances(@Nullable List<Balance> balances) {
            this.balances.clear();
            ViewUtils.whenNonNull(balances, this.balances::addAll);
            notifyDataSetChanged();
        }

        void restoreState(@NonNull Bundle savedState) {
            balances.addAll(savedState.getParcelableArrayList(BALANCES_KEY));
        }

        void saveState(@NonNull Bundle saveState) {
            saveState.putParcelableArrayList(BALANCES_KEY, balances);
        }
    }

    class BalanceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.asset_code)
        TextView assetCode;

        @BindView(R.id.asset_volume)
        TextView assetVolume;

        BalanceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(@NonNull Balance balance) {
            assetCode.setText(balance.getAsset_code());
            assetVolume.setText(String.valueOf(balance.getBalance()));
        }
    }
}
