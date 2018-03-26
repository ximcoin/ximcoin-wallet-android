package com.ximcoin.ximwallet.view.contacts;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.reactivex.functions.Action;
import petrov.kristiyan.colorpicker.ColorPicker;
import com.ximcoin.ximwallet.R;
import com.ximcoin.ximwallet.model.persistence.contacts.Contact;
import com.ximcoin.ximwallet.presenter.contacts.EditContactContract;
import com.ximcoin.ximwallet.view.common.BackPressListener;
import com.ximcoin.ximwallet.view.common.BaseViewFragment;
import com.ximcoin.ximwallet.view.util.TextUtils;
import com.ximcoin.ximwallet.view.util.ViewUtils;
import timber.log.Timber;

public class EditContactFragment extends BaseViewFragment<EditContactContract.EditContactPresenter>
        implements EditContactContract.EditContactView, BackPressListener {
    private static final String CONTACT_ARG = "EditContactFragment.CONTACT_ARG";
    private static final String COLOR_INDEX_TAG = "EditContactFragment.COLOR_INDEX";

    @BindView(R.id.name_field_layout)
    TextInputLayout nameFieldContainer;

    @BindView(R.id.name_field)
    TextInputEditText name;

    @BindView(R.id.address_field_layout)
    TextInputLayout addressFieldContainer;

    @BindView(R.id.address_field)
    TextInputEditText address;

    @BindView(R.id.notes_field)
    TextInputEditText notes;

    @BindView(R.id.contact_color)
    ImageButton contactColor;

    private int colorIndex = 0;

    public static EditContactFragment getInstance(@Nullable Contact contact) {
        Bundle args = new Bundle();
        args.putParcelable(CONTACT_ARG, contact);
        EditContactFragment fragment = new EditContactFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_contact_fragment, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            // Not the first time this fragment has started. Presenter relies on inherent save state
            // of the fields for cross-instance preservation of pending changes. The only thing
            // that isn't inherently saved is the color index.
            colorIndex = savedInstanceState.getInt(COLOR_INDEX_TAG, 0);
            updateContactColor();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(COLOR_INDEX_TAG, colorIndex);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewUtils.whenNonNull(getActivity(), activity ->
                ((ContactsFlowManager) activity).setTitle(getString(getTitleResource())));
    }

    private int getTitleResource() {
        return getContact() == null ?
                R.string.add_contact_fragment_title
                : R.string.edit_contact_fragment_title;
    }

    @Nullable
    @Override
    public Contact getContact() {
        Bundle args = getArguments();
        return args == null ? null : args.getParcelable(CONTACT_ARG);
    }

    @Override
    public void displayContactContents(@NonNull Contact contact) {
        colorIndex = contact.getColorIndex();

        name.setText(contact.getName());
        address.setText(contact.getAddress());
        notes.setText(contact.getNotes());
        updateContactColor();
    }

    private void updateContactColor() {
        contactColor.setColorFilter(
                ContactUtil.getContactColor(activityContext, colorIndex),
                PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void showDiscardChangesConfirmation() {
        new AlertDialog.Builder(activityContext, R.style.DefaultAlertDialog)
                .setTitle(R.string.discard_changes_title)
                .setMessage(R.string.discard_changes_message)
                .setNegativeButton(R.string.yes, ((dialog, which) ->
                        presenter.onUserConfirmedDiscardChanges()))
                .setPositiveButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void showBlockedLoading() {
        ViewUtils.whenNonNull(getActivity(), activity ->
                ((ContactsFlowManager) activity).showBlockedLoading(
                        getString(R.string.saving_contact_progress_message)));
    }

    @Override
    public void hideBlockedLoading(boolean wasSuccess, boolean shouldGoBack) {
        Action backAction = shouldGoBack ? this::goBack : null;
        int messageResource = wasSuccess ?
                R.string.saving_contact_progress_success
                : R.string.saving_contact_progress_fail;

        ViewUtils.whenNonNull(getActivity(), activity ->
                ((ContactsFlowManager) activity).hideBlockedLoading(
                        getString(messageResource),
                        wasSuccess,
                        false,
                        backAction));
    }

    @Override
    public void goBack() {
        ViewUtils.whenNonNull(getActivity(), activity -> ((ContactsFlowManager) activity).goBack());
    }

    @Override
    public void showError(EditContactContract.EditContactPresenter.EditContactError error) {
        switch (error) {
            case NAME_EMPTY:
                nameFieldContainer.setError(getString(R.string.contact_name_empty_error));
                break;
            case ADDRESS_LENGTH:
                addressFieldContainer.setError(getString(R.string.account_address_length_error,
                        getResources().getInteger(R.integer.address_length)));
                break;
            case ADDRESS_PREFIX:
                addressFieldContainer.setError(getString(R.string.account_address_prefix_error));
                break;
            case ADDRESS_FORMAT:
                addressFieldContainer.setError(getString(R.string.account_address_format_error));
                break;
            default:
                Timber.e("Unhandled error: %s", error.name());
                break;
        }
    }

    @OnTextChanged(R.id.name_field)
    public void onNameContentsChanged(Editable editable) {
        nameFieldContainer.setError(null);
    }

    @OnTextChanged(R.id.address_field)
    public void onAddressContentsChanged(Editable editable) {
        addressFieldContainer.setError(null);
    }

    @OnClick(R.id.btn_save_contact)
    public void onUserRequestSave() {
        presenter.onUserRequestSave(
                name.getText().toString(),
                address.getText().toString(),
                notes.getText().toString(),
                colorIndex);
    }

    @OnClick(R.id.contact_color)
    public void onUserRequestColorChange() {
        if (getActivity() == null) {
            return;
        }

        // TODO: Color picker doesn't seem reusable. Should lazy init and reuse.
        ColorPicker colorPicker = new ColorPicker(getActivity());
        colorPicker.getDialogBaseLayout().setBackgroundColor(ContextCompat.getColor(activityContext, R.color.mainBackground));
        colorPicker.setDefaultColorButton(ContextCompat.getColor(activityContext, R.color.white));
        colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
            @Override
            public void setOnFastChooseColorListener(int position, int color) {
                colorIndex = position;
                updateContactColor();
            }

            @Override
            public void onCancel() {

            }
        });
        colorPicker.setColors(R.array.contact_colors);
        colorPicker.setTitle(getString(R.string.contact_color_dialog_title));

        colorPicker.show();
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
                address.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        presenter.onUserRequestGoBack(
                name.getText().toString(),
                address.getText().toString(),
                notes.getText().toString(),
                colorIndex);
    }
}
