package tech.duchess.luminawallet.view.account;

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
import tech.duchess.luminawallet.LuminaWalletApp;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.api.HelpLinks;
import tech.duchess.luminawallet.model.api.HorizonApi;
import tech.duchess.luminawallet.model.fees.Fees;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.util.AssetUtil;
import tech.duchess.luminawallet.view.util.ViewUtils;

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
        LuminaWalletApp.getInstance()
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
            showAccountNotOnNetwork();
        } else {
            showAccount(account);
        }
    }

    private void showNoAccountsFound() {
        setVisibility(GONE);
    }

    private void showAccountNotOnNetwork() {
        setVisibility(VISIBLE);
        balanceLabel.setVisibility(GONE);
        lumenBalance.setVisibility(GONE);
        notOnNetworkContainer.setVisibility(VISIBLE);

        if (fees == null) {
            notOnNetworkMessage.setText(getResources()
                    .getString(R.string.account_not_on_network_unknown_fee_message));
            loadFees();
        } else {
            setNotOnNetworkFee();
        }
    }

    private void loadFees() {
        horizonApi.getFees()
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposables::add)
                .subscribe(feesWrapper -> {
                    fees = feesWrapper.getFees();
                    setNotOnNetworkFee();
                });
    }

    private void setNotOnNetworkFee() {
        if (fees != null) {
            String minBalance =
                    AssetUtil.getAssetAmountString(Double.parseDouble(fees.getBase_reserve()) * 2);
            minBalance = getResources().getQuantityString(R.plurals.lumens, 1, minBalance);
            notOnNetworkMessage.setText(getResources()
                    .getString(R.string.account_not_on_network_message, minBalance));
        }
    }

    private void showAccount(@NonNull Account account) {
        setVisibility(VISIBLE);
        balanceLabel.setVisibility(VISIBLE);
        lumenBalance.setVisibility(VISIBLE);
        notOnNetworkContainer.setVisibility(GONE);

        lumenBalance.setText(AssetUtil.getAssetAmountString(account.getLumens().getBalance()));
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
