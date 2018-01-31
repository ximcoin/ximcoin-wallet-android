package tech.duchess.luminawallet.view.account.send;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.api.HelpLinks;
import tech.duchess.luminawallet.presenter.account.send.TransactionSummary;
import tech.duchess.luminawallet.view.common.BaseFragment;
import tech.duchess.luminawallet.view.util.ViewUtils;
import timber.log.Timber;

public class SendConfirmationFragment extends BaseFragment {
    private static final String TRANSACTION_SUMMARY_KEY =
            "SendConfirmationFragment.TRANSACTION_SUMMARY_KEY";

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.send_amount)
    TextView sendAmount;

    @BindView(R.id.recipient_address)
    TextView recipientAddress;

    @BindView(R.id.transaction_fee)
    TextView transactionFee;

    @BindView(R.id.remaining_balances)
    TextView remainingBalances;

    @BindView(R.id.info_message_container)
    View infoMessageContainer;

    @BindView(R.id.info_message)
    TextView infoMessage;

    @BindView(R.id.password_field_layout)
    TextInputLayout passwordLayout;

    @BindView(R.id.password_field)
    TextInputEditText passwordField;

    @BindView(R.id.confirm_button)
    Button confirmButton;

    @BindView(R.id.cancel_button)
    Button cancelButton;

    private TransactionSummary transactionSummary;

    public static SendConfirmationFragment newInstance(@NonNull TransactionSummary transactionSummary) {
        Bundle args = new Bundle();
        args.putParcelable(TRANSACTION_SUMMARY_KEY, transactionSummary);
        SendConfirmationFragment fragment = new SendConfirmationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.send_confirmation_fragment, container, false);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            Timber.e("Arguments were empty");
            dismiss();
            return;
        }

        transactionSummary = args.getParcelable(TRANSACTION_SUMMARY_KEY);

        if (transactionSummary == null) {
            Timber.e("Transaction summary was empty");
            dismiss();
            return;
        }

        init();
    }

    private void init() {
        title.setText(transactionSummary.isCreatingAccount()
                ? R.string.create_account_title
                : R.string.send_payment_title);
        sendAmount.setText(getString(R.string.asset_balance,
                Double.toString(transactionSummary.getSendAmount()),
                transactionSummary.getSendingAssetCode()));
        recipientAddress.setText(transactionSummary.getRecipient());
        transactionFee.setText(getResources().getQuantityString(R.plurals.lumens,
                (int) transactionSummary.getTransactionFees(),
                transactionSummary.getTransactionFees()));
        addBalances();
        checkViolations();
    }

    private void addBalances() {
        LinkedHashMap<String, Double> balanceMap = transactionSummary.getRemainingBalances();
        List<String> assets = new ArrayList<>(balanceMap.keySet());
        StringBuilder balances = new StringBuilder();
        int assetsSize = assets.size();
        for (int i = 0; i < assetsSize; i++) {
            String asset = assets.get(i);
            balances.append(getString(R.string.asset_balance,
                    Double.toString(balanceMap.get(asset)), asset));

            if (i != assetsSize - 1) {
                balances.append("\n");
            }
        }

        remainingBalances.setText(balances.toString());
    }

    private void checkViolations() {
        if (transactionSummary.isSelfMinimumBalanceViolated()
                || !transactionSummary.isCreatedAccountMinimumBalanceMet()) {
            String info = "";
            boolean createBalanceViolated = transactionSummary.isCreatingAccount()
                    && !transactionSummary.isCreatedAccountMinimumBalanceMet();

            if (transactionSummary.isSelfMinimumBalanceViolated()) {
                info += getString(R.string.self_account_balance_violated,
                        Double.toString(transactionSummary.getSelfMinimumBalance()));

                if (createBalanceViolated) {
                    info += "\n";
                }
            }

            if (createBalanceViolated) {
                info += getString(R.string.new_account_balance_violated,
                        Double.toString(transactionSummary.getCreatedAccountMinimumBalance()));
            }

            infoMessage.setText(info);
            infoMessageContainer.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.GONE);
            confirmButton.setEnabled(true);
            confirmButton.setText(R.string.ok);
            cancelButton.setVisibility(View.GONE);
        } else {
            infoMessageContainer.setVisibility(View.GONE);
            passwordLayout.setVisibility(View.VISIBLE);
            confirmButton.setText(R.string.confirm_transaction);
            cancelButton.setVisibility(View.VISIBLE);
        }
    }

    @OnTextChanged(R.id.password_field)
    public void onUserChangedPassword(Editable editable) {
        if (!confirmButton.isEnabled()) {
            confirmButton.setEnabled(true);
        }
    }

    @OnClick(R.id.minimum_balance_info)
    public void onUserSelectedMinimumBalanceInfo() {
        ViewUtils.openUrl(HelpLinks.MINIMUM_ACCOUNT_BALANCE, activityContext);
    }

    @OnClick(R.id.confirm_button)
    public void onUserConfirmedTransaction() {
        if (transactionSummary == null
                || transactionSummary.isSelfMinimumBalanceViolated()
                || !transactionSummary.isCreatedAccountMinimumBalanceMet()) {
            dismiss();
        } else {
            dismiss();
            ViewUtils.whenNonNull(getParentFragment(), parentFragment -> {
                if (parentFragment instanceof TransactionConfirmationListener) {
                    ((TransactionConfirmationListener) parentFragment)
                            .onTransactionConfirmed(passwordField.getText().toString());
                }
            });
        }
    }

    @OnClick(R.id.cancel_button)
    public void onUserCanceledTransaction() {
        dismiss();
    }

    public interface TransactionConfirmationListener {
        void onTransactionConfirmed(@Nullable String password);
    }
}
