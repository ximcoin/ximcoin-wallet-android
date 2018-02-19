package tech.duchess.luminawallet.view.account.send;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.presenter.account.AccountsContract.AccountsView;
import tech.duchess.luminawallet.presenter.account.send.SendContract;
import tech.duchess.luminawallet.presenter.account.send.TransactionSummary;
import tech.duchess.luminawallet.view.account.IAccountPerspectiveView;
import tech.duchess.luminawallet.view.common.BaseViewFragment;
import tech.duchess.luminawallet.view.util.TextUtils;

public class SendFragment extends BaseViewFragment<SendContract.SendPresenter>
        implements IAccountPerspectiveView, SendContract.SendView,
        SendConfirmationFragment.TransactionConfirmationListener {
    private static final String ACCOUNT_KEY = "SendFragment.ACCOUNT_KEY";

    private static final InputFilter ASCII_FILTER = (source, start, end, dest, dstart, dend) -> {
        SpannableStringBuilder ret;

        if (source instanceof SpannableStringBuilder) {
            ret = (SpannableStringBuilder) source;
        } else {
            ret = new SpannableStringBuilder(source);
        }

        for (int i = end - 1; i >= start; i--) {
            char currentChar = source.charAt(i);
            if (currentChar > 127) {
                ret.delete(i, i + 1);
            }
        }

        return ret;
    };

    @BindView(R.id.recipient_field)
    TextInputEditText recipient;

    @BindView(R.id.amount_field)
    TextInputEditText amountField;

    @BindView(R.id.memo_field)
    TextInputEditText memoField;

    @BindView(R.id.unit_spinner)
    Spinner currencyUnitSpinner;

    private DialogFragment confirmationFragment;

    public static SendFragment newInstance(@Nullable Account account) {
        Bundle args = new Bundle();
        args.putParcelable(ACCOUNT_KEY, account);
        SendFragment fragment = new SendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.send_fragment, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        memoField.setFilters(new InputFilter[]{ASCII_FILTER});

        if (savedInstanceState == null) {
            Bundle args = getArguments();
            presenter.onAccountUpdated(args == null ? null : args.getParcelable(ACCOUNT_KEY));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (confirmationFragment != null) {
            confirmationFragment.dismiss();
            confirmationFragment = null;
        }
    }

    @Override
    public void setAccount(@Nullable Account account) {
        presenter.onAccountUpdated(account);
    }

    @OnClick(R.id.send_payment_button)
    public void onUserSendPayment() {
        presenter.onUserSendPayment(recipient.getText().toString(),
                amountField.getText().toString(),
                String.valueOf(currencyUnitSpinner.getSelectedItem()),
                memoField.getText().toString());
    }

    @Override
    public void showBlockedLoading(boolean isLoading,
                                   boolean isBuildingTransaction,
                                   boolean wasSuccess) {
        AccountsView accountsView = (AccountsView) activityContext;
        if (isLoading) {
            accountsView.showBlockedLoading(getString(isBuildingTransaction ?
                            R.string.loading_building_transaction
                            : R.string.loading_sending_payment));
        } else {
            int successResource;
            if (wasSuccess) {
                // We immediately hide overlay on success, since the transaction confirmation
                // dialog is coming.
                successResource = isBuildingTransaction ? 0 : R.string.send_payment_success;
            } else {
                successResource = isBuildingTransaction ? R.string.build_transaction_fail
                        : R.string.send_payment_fail;
            }

            String successMessage = successResource == 0 ? null : getString(successResource);
            accountsView.hideBlockedLoading(successMessage, wasSuccess,
                    isBuildingTransaction && wasSuccess);
        }
    }

    @Override
    public void showError(@NonNull SendContract.SendPresenter.SendError error) {

    }

    @Override
    public void showConfirmation(@NonNull TransactionSummary transactionSummary) {
        confirmationFragment = SendConfirmationFragment.newInstance(transactionSummary);
        confirmationFragment.show(childFragmentManager,
                SendConfirmationFragment.class.getSimpleName());
    }

    @Override
    public void showNoAccount() {

    }

    @Override
    public void setAvailableCurrencies(List<String> currencySelection) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activityContext,
                android.R.layout.simple_spinner_item,
                currencySelection);
        currencyUnitSpinner.setAdapter(adapter);
    }

    @Override
    public void clearForm() {
        recipient.setText(null);
        amountField.setText(null);
        memoField.setText(null);
    }

    @Override
    public void showTransactionSuccess(@NonNull Account account) {
        ((AccountsView) activityContext).onTransactionPosted(account);
    }

    @Override
    public void onTransactionConfirmed(@Nullable String password) {
        presenter.onUserConfirmPayment(password);
    }

    @OnClick(R.id.take_picture)
    public void onUserRequestQRScanner() {
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(this);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (!TextUtils.isEmpty(result.getContents())) {
                recipient.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
