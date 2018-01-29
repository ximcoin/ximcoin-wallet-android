package tech.duchess.luminawallet.view.account.send;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.presenter.account.AccountsContract;
import tech.duchess.luminawallet.presenter.account.send.SendContract;
import tech.duchess.luminawallet.view.account.IAccountPerspectiveView;
import tech.duchess.luminawallet.view.common.BaseViewFragment;

public class SendFragment extends BaseViewFragment<SendContract.SendPresenter>
        implements IAccountPerspectiveView, SendContract.SendView {
    private static final String ACCOUNT_KEY = "SendFragment.ACCOUNT_KEY";

    private static final InputFilter ASCII_FILTER = (source, start, end, dest, dstart, dend) -> {
        SpannableStringBuilder ret;

        if (source instanceof SpannableStringBuilder) {
            ret = (SpannableStringBuilder)source;
        } else {
            ret = new SpannableStringBuilder(source);
        }

        for (int i = end - 1; i >= start; i--) {
            char currentChar = source.charAt(i);
            if (currentChar > 127) {
                ret.delete(i, i+1);
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

    @BindView(R.id.pick_contact)
    ImageView pickContact;

    @BindView(R.id.take_picture)
    ImageView qrCode;

    @BindView(R.id.unit_spinner)
    Spinner currencyUnitSpinner;

    @BindView(R.id.send_payment_button)
    Button sendPaymentButton;

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

        if (savedInstanceState != null) {
            Bundle args = getArguments();
            presenter.onAccountUpdated(args == null ? null : args.getParcelable(ACCOUNT_KEY));
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
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull SendContract.SendPresenter.SendError error) {

    }

    @Override
    public void showNewAccountWarning() {

    }

    @Override
    public void showPaymentSummaryConfirmation() {
        presenter.onUserConfirmPayment("badpassword");
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

    }

    @Override
    public void showTransactionSuccess(@NonNull Account account) {
        ((AccountsContract.AccountsView) activityContext).onTransactionPosted(account);
    }
}
