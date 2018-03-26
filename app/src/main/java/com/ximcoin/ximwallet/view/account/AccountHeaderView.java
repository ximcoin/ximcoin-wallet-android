package com.ximcoin.ximwallet.view.account;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import com.ximcoin.ximwallet.XimWalletApp;
import com.ximcoin.ximwallet.R;
import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.api.HelpLinks;
import com.ximcoin.ximwallet.model.api.HorizonApi;
import com.ximcoin.ximwallet.model.fees.Fees;
import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.model.util.AssetUtil;
import com.ximcoin.ximwallet.model.util.FeesUtil;
import com.ximcoin.ximwallet.view.util.ViewUtils;

public class AccountHeaderView extends FrameLayout {
    private static final String SUPER_STATE_KEY = "AccountHeaderView.SUPER_STATE_KEY";
    private static final String FEES_KEY = "AccountHeaderView.FEES_KEY";

    @Inject
    HorizonApi horizonApi;

    @Inject
    SchedulerProvider schedulerProvider;

    @BindView(R.id.balance_label)
    View balanceLabel;

    @BindView(R.id.lumen_balance)
    TextView lumenBalance;

    @BindView(R.id.account_not_on_network_container)
    View notOnNetworkContainer;

    @BindView(R.id.account_not_on_network_message)
    TextView notOnNetworkMessage;

    @NonNull
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Nullable
    Fees fees;

    private Unbinder unbinder;

    public AccountHeaderView(Context context) {
        super(context);
        initView();
    }

    public AccountHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AccountHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        XimWalletApp.getInstance()
                .getAppComponent()
                .inject(this);
        addView(inflate(getContext(), R.layout.account_header_view, null));
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unbinder.unbind();
        disposables.dispose();
    }

    @OnClick(R.id.account_not_on_network_info)
    public void onNotOnNetworkInfoClicked() {
        ViewUtils.openUrl(HelpLinks.MINIMUM_ACCOUNT_BALANCE, getContext());
    }

    public void setAccount(@Nullable Account account) {
        if (account == null) {
            showNoAccountsFound();
        } else if (!account.isOnNetwork()) {
            showAccountNotOnNetwork(account);
        } else {
            showAccount(account);
        }
    }

    private void showNoAccountsFound() {
        setVisibility(GONE);
    }

    private void showAccountNotOnNetwork(@NonNull Account account) {
        setVisibility(VISIBLE);
        balanceLabel.setVisibility(GONE);
        lumenBalance.setVisibility(GONE);
        notOnNetworkContainer.setVisibility(VISIBLE);

        if (fees == null) {
            notOnNetworkMessage.setText(getResources()
                    .getString(R.string.account_not_on_network_unknown_fee_message));
            loadFees(account);
        } else {
            setNotOnNetworkFee(account);
        }
    }

    private void loadFees(@NonNull Account account) {
        horizonApi.getFees()
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposables::add)
                .subscribe(feesWrapper -> {
                    fees = feesWrapper.getFees();
                    setNotOnNetworkFee(account);
                });
    }

    private void setNotOnNetworkFee(@NonNull Account account) {
        if (fees != null) {
            // Show minimum balance to send as typical account minimum balance (times 2, to account
            // for trustline for XIM and some remaining balance for transactions).
            String minBalance = AssetUtil.getAssetAmountString(
                    FeesUtil.getMinimumAccountBalance(fees, account) * 2);
            minBalance = getResources().getQuantityString(R.plurals.lumens, 2, minBalance);
            notOnNetworkMessage.setText(getResources()
                    .getString(R.string.account_not_on_network_message, minBalance));
        }
    }

    private void showAccount(@NonNull Account account) {
        setVisibility(GONE);
        /*balanceLabel.setVisibility(VISIBLE);
        lumenBalance.setVisibility(VISIBLE);
        notOnNetworkContainer.setVisibility(GONE);

        lumenBalance.setText(AssetUtil.getAssetAmountString(account.getLumens().getBalance()));*/
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle saveState = new Bundle();
        saveState.putParcelable(SUPER_STATE_KEY, superState);
        saveState.putParcelable(FEES_KEY, fees);
        return saveState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof Bundle)) {
            super.onRestoreInstanceState(state);
            return;
        }

        Bundle savedState = (Bundle) state;
        super.onRestoreInstanceState(savedState.getParcelable(SUPER_STATE_KEY));
        fees = savedState.getParcelable(FEES_KEY);
    }
}
