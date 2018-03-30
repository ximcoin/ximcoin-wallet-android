package com.ximcoin.ximwallet.view.account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ximcoin.ximwallet.R;
import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.presenter.account.AccountsContract;
import com.ximcoin.ximwallet.presenter.account.TrustXimContract;
import com.ximcoin.ximwallet.view.common.BaseViewFragment;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class TrustXimFragment extends BaseViewFragment<TrustXimContract.TrustXimPresenter>
        implements TrustXimContract.TrustXimView {
    private static final String ACCOUNT_ARG = "TrustXimFragment.ACCOUNT_ARG";

    @BindView(R.id.password_field_layout)
    TextInputLayout passwordLayout;

    @BindView(R.id.password_field)
    TextInputEditText passwordField;

    @BindView(R.id.continue_button)
    Button continueButton;

    public static TrustXimFragment getInstance(@NonNull Account account) {
        TrustXimFragment fragment = new TrustXimFragment();
        Bundle args = new Bundle();
        args.putParcelable(ACCOUNT_ARG, account);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.trust_xim_fragment, container, false);
    }

    @Override
    public void showTrustTransactionInProgress() {
        ((AccountsContract.AccountsView) activityContext).showBlockedLoading(getString(R.string.trust_transaction_progress));
    }

    @Override
    public void hideTrustTransactionProgress(boolean wasSuccess) {
        AccountsContract.AccountsView accountsView = (AccountsContract.AccountsView) activityContext;
        if (wasSuccess) {
            accountsView.hideBlockedLoading(getString(R.string.trust_transaction_success),
                    true,
                    false);
            accountsView.refresh();
        } else {
            accountsView.hideBlockedLoading(getString(R.string.trust_transaction_failure),
                    false,
                    false);
        }
    }

    @Override
    public void showTransactionBuildFailure() {
        Toast.makeText(activityContext, R.string.trust_transaction_failure, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public Account getCurrentAccount() {
        Bundle args = getArguments();
        return args == null ? null : args.getParcelable(ACCOUNT_ARG);
    }

    @Override
    public void showPasswordInvalidError() {
        passwordLayout.setError(getString(R.string.invalid_password_error));
    }

    @OnTextChanged(R.id.password_field)
    public void onPasswordContentsChanged() {
        passwordLayout.setError(null);
        if (!continueButton.isEnabled()) {
            continueButton.setEnabled(true);
        }
    }

    @OnClick(R.id.continue_button)
    public void onUserRequestContinue() {
        presenter.trustXim(passwordField.getText().toString());
    }
}
