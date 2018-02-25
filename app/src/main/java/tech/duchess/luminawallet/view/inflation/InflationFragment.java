package tech.duchess.luminawallet.view.inflation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.OnClick;
import tech.duchess.luminawallet.EnvironmentConstants;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.api.HelpLinks;
import tech.duchess.luminawallet.presenter.inflation.InflationContract;
import tech.duchess.luminawallet.presenter.inflation.InflationContract.InflationPresenter.InflationError;
import tech.duchess.luminawallet.view.common.BaseViewFragment;
import tech.duchess.luminawallet.view.util.TextUtils;
import tech.duchess.luminawallet.view.util.ViewUtils;

public class InflationFragment extends BaseViewFragment<InflationContract.InflationPresenter>
        implements InflationContract.InflationView {
    private static final String ACCOUNT_ID_ARG = "InflationFragment.ACCOUNT_ID_ARG";

    @BindView(R.id.scroll_view)
    ScrollView scrollView;


    @BindView(R.id.inflation_field_layout)
    TextInputLayout inflationFieldLayout;

    @BindView(R.id.inflation_field)
    TextInputEditText inflationField;

    @BindView(R.id.fee_progress_bar)
    ProgressBar feeProgressBar;

    @BindView(R.id.fee)
    TextView fee;

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

    @Nullable
    @Override
    public String getAccountId() {
        Bundle args = getArguments();
        return args == null ? null : args.getString(ACCOUNT_ID_ARG);
    }

    @Override
    public void showCurrentInflationAddress(@Nullable String inflationDestination) {

    }

    @Override
    public void showOperationConfirmation() {

    }

    @Override
    public void showFeeLoading() {
        fee.setVisibility(View.GONE);
        feeProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFeeLoading(double setInflationFee) {
        fee.setText(Double.toString(setInflationFee));
        fee.setVisibility(View.VISIBLE);
        feeProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showFeeLoadFailed() {
        Toast.makeText(activityContext, R.string.inflation_fee_load_error, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showInflationRemovalConfirmation() {

    }

    @Override
    public void showBlockedLoading() {

    }

    @Override
    public void hideBlockedLoading(boolean wasSuccess) {

    }

    @Override
    public void showNoAccountError() {
        // This should never really happen, but satisfies nullability possibilities.
        Toast.makeText(activityContext, R.string.inflation_no_account_error, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showInsufficientFundsError(double minimumBalance) {

    }

    @Override
    public void showInflationError(@NonNull InflationError inflationError) {

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
}
