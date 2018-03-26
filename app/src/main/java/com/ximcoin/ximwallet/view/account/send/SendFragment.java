package com.ximcoin.ximwallet.view.account.send;

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
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import com.ximcoin.ximwallet.R;
import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.model.persistence.contacts.Contact;
import com.ximcoin.ximwallet.presenter.account.AccountsContract.AccountsView;
import com.ximcoin.ximwallet.presenter.account.send.SendContract;
import com.ximcoin.ximwallet.presenter.account.send.TransactionSummary;
import com.ximcoin.ximwallet.view.account.AccountPerspectiveView;
import com.ximcoin.ximwallet.view.common.BaseViewFragment;
import com.ximcoin.ximwallet.view.contacts.ContactsActivity;
import com.ximcoin.ximwallet.view.contacts.ContactsFlowManager;
import com.ximcoin.ximwallet.view.util.TextUtils;
import com.ximcoin.ximwallet.view.util.ViewUtils;

public class SendFragment extends BaseViewFragment<SendContract.SendPresenter>
        implements AccountPerspectiveView, SendContract.SendView,
        SendConfirmationFragment.TransactionConfirmationListener {
    private static final String ACCOUNT_KEY = "SendFragment.ACCOUNT_KEY";
    private static final int SELECT_CONTACT_REQUEST_CODE = 1;

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

    @BindView(R.id.recipient_field_layout)
    TextInputLayout recipientLayout;

    @BindView(R.id.recipient_field)
    TextInputEditText recipient;

    @BindView(R.id.amount_field_layout)
    TextInputLayout amountLayout;

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

    @Override
    public void transactionPostedForAccount(@NonNull Account account) {
        presenter.onAccountUpdated(account);
    }

    @OnClick(R.id.send_payment_button)
    public void onUserSendPayment() {
        recipientLayout.setError(null);
        amountLayout.setError(null);
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
        new Handler(Looper.getMainLooper()).post(() -> {
            switch (error) {
                case ADDRESS_INVALID:
                    recipientLayout.setError(getString(R.string.account_address_format_error));
                    break;
                case DEST_SAME_AS_SOURCE:
                    recipientLayout.setError(getString(R.string.account_address_same_as_self_error));
                    break;
                case ADDRESS_DOES_NOT_EXIST:
                    recipientLayout.setError(getString(R.string.account_address_not_exist_error));
                    break;
                case ADDRESS_BAD_LENGTH:
                    recipientLayout.setError(getString(R.string.account_address_length_error,
                            getResources().getInteger(R.integer.address_length)));
                    break;
                case ADDRESS_BAD_PREFIX:
                    recipientLayout.setError(getString(R.string.account_address_prefix_error));
                    break;
                case ADDRESS_UNSUPPORTED_CURRENCY:
                    recipientLayout.setError(getString(R.string.account_address_unsupported_asset_error));
                    break;
                case INSUFFICIENT_FUNDS:
                    amountLayout.setError(getString(R.string.insufficient_funds_error));
                    break;
                case AMOUNT_GREATER_THAN_ZERO:
                    amountLayout.setError(getString(R.string.amount_greater_than_zero_error));
                    break;
                case PASSWORD_INVALID:
                    Toast.makeText(activityContext, R.string.invalid_password_error, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @OnTextChanged(R.id.recipient_field)
    public void onRecipientContentsChanged(Editable editable) {
        recipientLayout.setError(null);
    }

    @OnTextChanged(R.id.amount_field)
    public void onAmountContentsChanged(Editable editable) {
        amountLayout.setError(null);
    }

    @OnItemSelected(R.id.unit_spinner)
    public void onCurrencyUnitChanged(Spinner spinner, int pos) {
        amountLayout.setError(null);
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
                R.layout.spinner_item,
                currencySelection);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        currencyUnitSpinner.setAdapter(adapter);
    }

    @Override
    public void clearForm() {
        recipient.setText(null);
        amountField.setText(null);
        memoField.setText(null);
    }

    @Override
    public void showTransactionSuccess(@NonNull Account account, @NonNull String destination) {
        ((AccountsView) activityContext).onTransactionPosted(account, destination);
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

    @OnClick(R.id.pick_contact)
    public void onUserRequestPickContact() {
        startActivityForResult(ContactsActivity.createIntentForContactSelection(activityContext),
                SELECT_CONTACT_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_CONTACT_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            Contact selectedContact =
                    data.getParcelableExtra(ContactsFlowManager.CONTACT_SELECTION_RESULT_KEY);
            ViewUtils.whenNonNull(selectedContact, contact ->
                    recipient.setText(contact.getAddress()));
            return;
        }

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
