package tech.duchess.luminawallet.view.inflation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.reactivex.functions.Action;
import tech.duchess.luminawallet.EnvironmentConstants;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.api.HelpLinks;
import tech.duchess.luminawallet.presenter.inflation.InflationContract;
import tech.duchess.luminawallet.presenter.inflation.InflationContract.InflationPresenter.InflationError;
import tech.duchess.luminawallet.presenter.inflation.InflationOperationSummary;
import tech.duchess.luminawallet.view.common.BaseViewFragment;
import tech.duchess.luminawallet.view.util.TextUtils;
import tech.duchess.luminawallet.view.util.ViewUtils;
import timber.log.Timber;

public class InflationFragment extends BaseViewFragment<InflationContract.InflationPresenter>
        implements InflationContract.InflationView, InflationConfirmationFragment.InflationConfirmationListener {
    private static final String ACCOUNT_ID_ARG = "InflationFragment.ACCOUNT_ID_ARG";

    @BindView(R.id.scroll_view)
    ScrollView scrollView;


    @BindView(R.id.inflation_field_layout)
    TextInputLayout inflationFieldLayout;

    @BindView(R.id.inflation_field)
    TextInputEditText inflationField;

    private DialogFragment confirmationFragment;

    static InflationFragment getInstance(@Nullable String accountId) {
        Bundle args = new Bundle();
        args.putString(ACCOUNT_ID_ARG, accountId);
        InflationFragment fragment = new InflationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.inflation_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((InflationFlowManager) activityContext)
                .setTitle(getString(R.string.inflation_fragment_title));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (confirmationFragment != null) {
            confirmationFragment.dismiss();
            confirmationFragment = null;
        }
    }

    @Nullable
    @Override
    public String getAccountId() {
        Bundle args = getArguments();
        return args == null ? null : args.getString(ACCOUNT_ID_ARG);
    }

    @Override
    public void showCurrentInflationAddress(@Nullable String inflationDestination) {
        inflationField.setText(inflationDestination);
    }

    @Override
    public void showTransactionConfirmation(@NonNull InflationOperationSummary inflationOperationSummary) {
        confirmationFragment = InflationConfirmationFragment.newInstance(inflationOperationSummary);
        confirmationFragment.show(childFragmentManager,
                InflationConfirmationFragment.class.getSimpleName());
    }

    @Override
    public void showLoading(boolean isLoading) {
        ((InflationFlowManager) activityContext).showLoading(isLoading);
    }

    @Override
    public void showBlockedLoading(boolean isBuildingTransaction) {
        ((InflationFlowManager) activityContext)
                .showBlockedLoading(getString(isBuildingTransaction ?
                        R.string.loading_building_transaction
                        : R.string.inflation_operation_loading));
    }

    @Override
    public void hideBlockedLoading(boolean wasBuildingTransaction, boolean wasSuccess) {
        Action action;
        String message;

        if (wasBuildingTransaction) {
            action = null;
            message = wasSuccess ? null : getString(R.string.build_transaction_fail);
        } else {
            if (wasSuccess) {
                action = () -> ((Activity) activityContext).onBackPressed();
                message = getString(R.string.inflation_operation_success);
            } else {
                action = null;
                message = getString(R.string.inflation_operation_fail);
            }
        }

        ((InflationFlowManager) activityContext).hideBlockedLoading(message, wasSuccess,
                wasBuildingTransaction && wasSuccess, action);
    }

    @Override
    public void showInsufficientFundsError(double minimumBalance) {
        inflationFieldLayout.setError(
                getString(R.string.self_account_balance_violated,
                        getResources().getQuantityString(R.plurals.lumens,
                                (int) minimumBalance, minimumBalance)));
    }

    @Override
    public void showInflationError(@NonNull InflationError inflationError) {
        new Handler(Looper.getMainLooper()).post(() -> {
            switch (inflationError) {
                case ADDRESS_LENGTH:
                    inflationFieldLayout.setError(getString(R.string.inflation_address_length_error,
                            getResources().getInteger(R.integer.address_length)));
                    break;
                case ADDRESS_PREFIX:
                    inflationFieldLayout.setError(getString(R.string.inflation_address_prefix_error));
                    break;
                case ADDRESS_FORMAT:
                    inflationFieldLayout.setError(getString(R.string.inflation_address_format_error));
                    break;
                case ALREADY_SET:
                    inflationFieldLayout.setError(getString(R.string.inflation_address_already_set_error));
                    break;
                case PASSWORD_LENGTH:
                    Toast.makeText(activityContext, R.string.invalid_password_error, Toast.LENGTH_SHORT)
                            .show();
                    break;
                default:
                    Timber.e("Unhandled inflation error: %s", inflationError.name());
                    break;
            }
        });
    }

    @OnTextChanged(R.id.inflation_field)
    public void onInflationFieldContentsChanged() {
        inflationFieldLayout.setError(null);
    }

    @OnClick(R.id.set_inflation_button)
    public void onUserRequestSetInflation() {
        presenter.onUserSetInflationDestination(inflationField.getText().toString());
    }

    @OnClick(R.id.populate_lumenaut_button)
    public void onUserRequestPopulateLumenautAddress() {
        inflationField.setText(EnvironmentConstants.LUMENAUT_INFLATION_ADDRESS);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    @OnClick(R.id.inflation_info)
    public void onUserRequestInflationInfo() {
        ViewUtils.openUrl(HelpLinks.INFLATION, activityContext);
    }

    @OnClick(R.id.lumenaut_info)
    public void onUserRequestLumenautInfo() {
        ViewUtils.openUrl(HelpLinks.LUMENAUT, activityContext);
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
                inflationField.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onInflationConfirmed(@Nullable String password) {
        presenter.onUserConfirmedTransaction(password);
    }
}
