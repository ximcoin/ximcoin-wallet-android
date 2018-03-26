package com.ximcoin.ximwallet.view.createaccount;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import com.ximcoin.ximwallet.R;
import com.ximcoin.ximwallet.presenter.createaccount.ImportSeedContract;
import com.ximcoin.ximwallet.view.common.BaseViewFragment;
import com.ximcoin.ximwallet.view.util.TextUtils;
import com.ximcoin.ximwallet.view.util.ViewUtils;
import timber.log.Timber;

public class ImportSeedFragment extends BaseViewFragment<ImportSeedContract.ImportSeedPresenter>
        implements ImportSeedContract.ImportSeedView {

    @BindView(R.id.seed_field_layout)
    TextInputLayout seedFieldLayout;

    @BindView(R.id.seed_field)
    TextInputEditText seedField;

    @BindView(R.id.warning_message)
    TextView warningMessage;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.import_seed_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        setSeedWarningMessage();
        return view;
    }

    private void setSeedWarningMessage() {
        ViewUtils.whenNonNull(getContext(), c ->
                warningMessage.setText(TextUtils.getBulletedList(
                        5,
                        null,
                        c,
                        R.string.seed_warning_bullet1,
                        R.string.seed_warning_bullet2_import,
                        R.string.seed_warning_bullet3,
                        R.string.seed_warning_bullet4)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewUtils.whenNonNull(getActivity(), activity ->
                ((CreateAccountFlowManager) activity)
                        .setTitle(getString(R.string.import_seed_fragment_title)));
    }

    @Override
    public void showError(@NonNull ImportSeedContract.ImportSeedPresenter.ImportError error) {
        String errorMessage;
        switch (error) {
            case SEED_LENGTH_ERROR:
                errorMessage = getString(R.string.seed_length_error,
                        getResources().getInteger(R.integer.address_length));
                break;
            case SEED_FORMAT_ERROR:
                errorMessage = getString(R.string.seed_format_error);
                break;
            case SEED_PREFIX_ERROR:
                errorMessage = getString(R.string.seed_prefix_error);
                break;
            default:
                Timber.e("Unhandled seed import error: %s", error.name());
                return;
        }

        seedFieldLayout.setError(errorMessage);
    }

    @Override
    public void clearError() {
        seedFieldLayout.setError(null);
    }

    @Override
    public void onSeedValidated(@NonNull String seed) {
        ViewUtils.whenNonNull(getActivity(), activity ->
                ((CreateAccountActivity) activity).onSeedCreated(seed));
    }

    @OnClick(R.id.btn_next)
    public void onUserRequestNext() {
        presenter.onUserImportSeed(seedField.getText().toString());
    }

    @OnTextChanged(R.id.seed_field)
    public void onTextChanged(Editable editable) {
        presenter.onSeedFieldContentsChanged();
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
                seedField.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
