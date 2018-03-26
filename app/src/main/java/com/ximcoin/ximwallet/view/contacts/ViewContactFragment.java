package com.ximcoin.ximwallet.view.contacts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import com.ximcoin.ximwallet.R;
import com.ximcoin.ximwallet.model.persistence.contacts.Contact;
import com.ximcoin.ximwallet.presenter.contacts.ViewContactContract;
import com.ximcoin.ximwallet.view.account.receive.SquareImageView;
import com.ximcoin.ximwallet.view.common.BaseViewFragment;
import com.ximcoin.ximwallet.view.util.TextUtils;
import com.ximcoin.ximwallet.view.util.ViewUtils;

public class ViewContactFragment extends BaseViewFragment<ViewContactContract.ViewContactPresenter>
        implements ViewContactContract.ViewContactView {
    private static final String CONTACT_ID_ARG = "ViewContactFragment.CONTACT_ID_ARG";

    @BindView(R.id.contact_header)
    View contactHeader;

    @BindView(R.id.qr_code)
    SquareImageView qrCode;

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.address)
    TextView address;

    @BindView(R.id.notes)
    TextView notes;

    @BindView(R.id.btn_copy_address)
    ImageButton copyAddressButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_contact_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewUtils.whenNonNull(getActivity(), activity ->
                ((ContactsFlowManager) activity)
                        .setTitle(getString(R.string.view_contact_fragment_title)));
    }

    @NonNull
    static ViewContactFragment getInstance(long contactId) {
        Bundle args = new Bundle();
        args.putLong(CONTACT_ID_ARG, contactId);
        ViewContactFragment fragment = new ViewContactFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public Long getContactId() {
        Bundle args = getArguments();
        return args == null ? null : args.getLong(CONTACT_ID_ARG);
    }

    @Override
    public void showContact(@NonNull Contact contact) {
        setMenuVisibility(true);

        name.setText(contact.getName());

        contactHeader.setBackgroundColor(ContactUtil.getContactColor(
                activityContext, contact.getColorIndex()));

        String contactAddress = contact.getAddress();
        if (TextUtils.isEmpty(contactAddress)) {
            qrCode.setVisibility(View.GONE);
            address.setText(R.string.contact_address_empty);
            copyAddressButton.setVisibility(View.GONE);
        } else {
            qrCode.setImageBitmap(ViewUtils.encodeAsBitmap(contactAddress));
            address.setText(contactAddress);
            copyAddressButton.setVisibility(View.VISIBLE);
        }

        String contactNotes = contact.getNotes();
        if (TextUtils.isEmpty(contactNotes)) {
            notes.setText(R.string.contact_notes_empty);
        } else {
            notes.setText(contactNotes);
        }
    }

    @Override
    public void showContactNotFound() {
        setMenuVisibility(false);
        qrCode.setVisibility(View.GONE);
        address.setVisibility(View.GONE);
        copyAddressButton.setVisibility(View.GONE);
        notes.setVisibility(View.GONE);
        name.setText(R.string.contact_load_error_message);
    }

    @Override
    public void goBack() {
        ViewUtils.whenNonNull(getActivity(), FragmentActivity::onBackPressed);
    }

    @Override
    public void showLoading(boolean isLoading) {
        ViewUtils.whenNonNull(getActivity(), activity ->
                ((ContactsFlowManager) activity).showLoading(isLoading));
    }

    @Override
    public void goToEditContact(@NonNull Contact contact) {
        ViewUtils.whenNonNull(getActivity(), activity ->
                ((ContactsFlowManager) activity).onEditContactRequested(contact));
    }

    @Override
    public void showDeleteFailed() {
        Toast.makeText(activityContext, R.string.contact_delete_error_message, Toast.LENGTH_SHORT)
                .show();
    }

    @OnClick(R.id.btn_copy_address)
    public void onUserRequestCopyAddress() {
        CharSequence contactAddress = address.getText();

        String toastMessage;
        if (ViewUtils.copyToClipboard(activityContext, getString(R.string.address_clipboard_label),
                contactAddress)) {
            toastMessage = getString(R.string.address_copied_success_toast);
        } else {
            toastMessage = getString(R.string.address_copied_failed_toast);
        }

        Toast.makeText(activityContext, toastMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_contact_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_contact:
                presenter.onUserRequestEdit();
                return true;
            case R.id.delete_contact:
                presenter.onUserRequestDelete();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showDeleteConfirmation() {
        new AlertDialog.Builder(activityContext, R.style.DefaultAlertDialog)
                .setTitle(R.string.delete_contact_confirmation_title)
                .setMessage(R.string.delete_contact_message)
                .setNegativeButton(R.string.yes, ((dialog, which) ->
                        presenter.onUserConfirmDelete()))
                .setPositiveButton(R.string.cancel, null)
                .show();
    }
}
