package com.ximcoin.ximwallet.view.account.balance;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ximcoin.ximwallet.R;
import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.api.HorizonApi;
import com.ximcoin.ximwallet.model.fees.Fees;
import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.model.util.AccountUtil;
import com.ximcoin.ximwallet.model.util.AssetUtil;
import com.ximcoin.ximwallet.view.account.AccountPerspectiveView;
import com.ximcoin.ximwallet.view.util.ViewUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import io.reactivex.disposables.CompositeDisposable;

public class BalancesFragment extends Fragment implements AccountPerspectiveView {
    private static final String ACCOUNT_KEY = "BalancesFragment.ACCOUNT_KEY";
    private static final String FEES_KEY = "BalancesFragment.FEES_KEY";

    @BindView(R.id.xim_balance)
    TextView ximBalance;

    @BindView(R.id.remaining_transactions)
    TextView remainingTransactions;

    @BindView(R.id.balances_container)
    ViewGroup balancesContainer;

    @Inject
    HorizonApi horizonApi;

    @Inject
    SchedulerProvider schedulerProvider;

    @Nullable
    private Fees fees;

    @Nullable
    private Account account;

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

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
        View view = inflater.inflate(R.layout.balances_fragment, container, false);
        AndroidSupportInjection.inject(this);
        unbinder = ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            fees = savedInstanceState.getParcelable(FEES_KEY);
            account = savedInstanceState.getParcelable(ACCOUNT_KEY);
            updateBalances(account);
        } else {
            ViewUtils.whenNonNull(getArguments(), args ->
                    ViewUtils.whenNonNull(args.getParcelable(ACCOUNT_KEY), account ->
                            updateBalances((Account) account)));
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(FEES_KEY, fees);
        outState.putParcelable(ACCOUNT_KEY, account);
    }

    @OnClick(R.id.remaining_transactions_help)
    public void onUserRequestRemainingTransactionsHelp() {
        Context context = getContext();

        ViewUtils.whenNonNull(context, c ->
                new AlertDialog.Builder(c, R.style.DefaultAlertDialog)
                        .setMessage(R.string.remaining_transactions_help_message)
                        .setPositiveButton(R.string.ok, null)
                        .show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        compositeDisposable.clear();
    }

    @Override
    public void setAccount(@Nullable Account account) {
        updateBalances(account);
    }

    @Override
    public void transactionPostedForAccount(@NonNull Account account) {
        updateBalances(account);
    }

    private void updateBalances(@Nullable Account account) {
        this.account = account;
        if (account == null) {
            balancesContainer.setVisibility(View.GONE);
        } else {
            balancesContainer.setVisibility(View.VISIBLE);
            ximBalance.setText(AssetUtil.getXimAmountString(account.getXimBalance()));
            if (fees != null) {
                updateRemainingTransactions(account, fees);
            } else {
                getFeesAndUpdate(account);
            }
        }
    }

    private void getFeesAndUpdate(@NonNull Account account) {
        remainingTransactions.setText(getString(R.string.remaining_transactions_loading_message));
        compositeDisposable.clear();
        horizonApi.getFees()
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(compositeDisposable::add)
                .subscribe(feesWrapper -> {
                    fees = feesWrapper.getFees();
                    updateRemainingTransactions(account, fees);
                }, throwable ->
                        Toast.makeText(getContext(),
                                getString(R.string.failed_remaining_transactions_load),
                                Toast.LENGTH_SHORT).show());
    }

    private void updateRemainingTransactions(@NonNull Account account,
                                             @NonNull Fees fees) {
        remainingTransactions.setText(AssetUtil.XIM_FORMAT.format(AccountUtil.getTransactionsRemaining(account, fees)));
    }
}
