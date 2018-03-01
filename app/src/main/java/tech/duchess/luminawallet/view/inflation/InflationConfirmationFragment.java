package tech.duchess.luminawallet.view.inflation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.util.AssetUtil;
import tech.duchess.luminawallet.presenter.inflation.InflationOperationSummary;
import tech.duchess.luminawallet.view.common.BaseFragment;
import tech.duchess.luminawallet.view.util.ViewUtils;
import timber.log.Timber;

public class InflationConfirmationFragment extends BaseFragment {
    private static final String INFLATION_OPERATION_ARG = "InflationConfirmationFragment.INFLATION_OPERATION_ARG";

    @BindView(R.id.inflation_destination)
    TextView inflationDestination;

    @BindView(R.id.transaction_fee)
    TextView transactionFee;

    @BindView(R.id.password_field)
    TextInputEditText passwordField;

    @BindView(R.id.confirm_button)
    Button confirmButton;

    private InflationOperationSummary inflationSummary;

    public static InflationConfirmationFragment newInstance(@NonNull InflationOperationSummary summary) {
        Bundle args = new Bundle();
        args.putParcelable(INFLATION_OPERATION_ARG, summary);
        InflationConfirmationFragment fragment = new InflationConfirmationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.inflation_confirmation_fragment, container,
                false);
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

        inflationSummary = args.getParcelable(INFLATION_OPERATION_ARG);

        if (inflationSummary == null) {
            Timber.e("Inflation summary was empty");
            dismiss();
            return;
        }

        init();
    }

    private void init() {
        inflationDestination.setText(inflationSummary.getInflationAddress());
        transactionFee.setText(AssetUtil.getAssetAmountString(inflationSummary.getFees()));
    }

    @OnTextChanged(R.id.password_field)
    public void onUserChangedPassword(Editable editable) {
        if (!confirmButton.isEnabled()) {
            confirmButton.setEnabled(true);
        }
    }

    @OnClick(R.id.confirm_button)
    public void onUserConfirmedTransaction() {
        dismiss();
        ViewUtils.whenNonNull(getParentFragment(), parentFragment -> {
            if (parentFragment instanceof InflationConfirmationListener) {
                ((InflationConfirmationListener) parentFragment)
                        .onInflationConfirmed(passwordField.getText().toString());
            } else {
                Timber.e("Parent fragment wasn't listening for inflation confirmation");
            }
        });
    }

    @OnClick(R.id.cancel_button)
    public void onUserCanceledTransaction() {
        dismiss();
    }

    public interface InflationConfirmationListener {
        void onInflationConfirmed(@Nullable String password);
    }
}
