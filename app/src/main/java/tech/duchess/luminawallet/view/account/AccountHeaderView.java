package tech.duchess.luminawallet.view.account;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.api.HelpLinks;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.util.AssetUtil;
import tech.duchess.luminawallet.view.util.ViewUtils;

public class AccountHeaderView extends FrameLayout {

    @BindView(R.id.balance_label)
    View balanceLabel;

    @BindView(R.id.lumen_balance)
    TextView lumenBalance;

    @BindView(R.id.account_not_on_network_container)
    View notOnNetworkContainer;

    @BindView(R.id.account_not_on_network_message)
    TextView notOnNetworkMessage;

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
        addView(inflate(getContext(), R.layout.account_header_view, null));
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unbinder.unbind();
    }

    @OnClick(R.id.account_not_on_network_info)
    public void onNotOnNetworkInfoClicked() {
        ViewUtils.openUrl(HelpLinks.MINIMUM_ACCOUNT_BALANCE, getContext());
    }

    public void setAccount(@Nullable Account account) {
        if (account == null) {
            showNoAccountsFound();
        } else if (!account.isOnNetwork()) {
            showAccountNotOnNetwork(account.getAccount_id());
        } else {
            showAccount(account);
        }
    }

    private void showNoAccountsFound() {
        // TODO:
        setVisibility(GONE);
    }

    private void showAccountNotOnNetwork(@NonNull String accountId) {
        setVisibility(VISIBLE);
        balanceLabel.setVisibility(GONE);
        lumenBalance.setVisibility(GONE);
        notOnNetworkContainer.setVisibility(VISIBLE);

        // TODO: Get this from backend
        String minBalance = getResources().getQuantityString(R.plurals.lumens, 1, 1);
        notOnNetworkMessage.setText(getResources()
                .getString(R.string.account_not_on_network_message, minBalance));
    }

    private void showAccount(@NonNull Account account) {
        setVisibility(VISIBLE);
        balanceLabel.setVisibility(VISIBLE);
        lumenBalance.setVisibility(VISIBLE);
        notOnNetworkContainer.setVisibility(GONE);

        lumenBalance.setText(AssetUtil.getAssetAmountString(account.getLumens().getBalance()));
    }
}
