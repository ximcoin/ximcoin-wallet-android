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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import butterknife.BindView;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.account.Account;
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
    }

    @Override
    public void setAccount(@Nullable Account account) {

    }
}
