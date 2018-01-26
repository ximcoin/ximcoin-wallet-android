package tech.duchess.luminawallet.view.account.receive;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.view.account.IAccountPerspectiveView;
import tech.duchess.luminawallet.view.util.TextUtils;
import tech.duchess.luminawallet.view.util.ViewUtils;

public class ReceiveFragment extends Fragment implements IAccountPerspectiveView {
    private static final String ACCOUNT_KEY = "ReceiveFragment.ACCOUNT_KEY";
    private static final String ADDRESS_KEY = "ReceiveFragment.ADDRESS_KEY";

    @BindView(R.id.qr_code)
    ImageView qrCode;

    @BindView(R.id.view_full_address_button)
    Button viewFullAddressButton;

    private String address;
    private Unbinder unbinder;

    public static ReceiveFragment newInstance(@Nullable Account account) {
        Bundle args = new Bundle();
        args.putParcelable(ACCOUNT_KEY, account);
        ReceiveFragment fragment = new ReceiveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.receive_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            Bundle args = getArguments();
            setAccount(args == null ? null : args.getParcelable(ACCOUNT_KEY));
        } else {
            setAddress(savedInstanceState.getParcelable(ADDRESS_KEY));
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ADDRESS_KEY, address);
    }

    @Override
    public void setAccount(@Nullable Account account) {
        setAddress(account == null ? null : account.getAccount_id());
    }

    private void setAddress(@Nullable String address) {
        this.address = address;
        if (TextUtils.isEmpty(address)) {
            qrCode.setImageDrawable(null);
            qrCode.setVisibility(View.GONE);
            viewFullAddressButton.setEnabled(false);
        } else {
            qrCode.setVisibility(View.VISIBLE);
            qrCode.setImageBitmap(ViewUtils.encodeAsBitmap(address));
            viewFullAddressButton.setEnabled(true);
        }
    }

    @OnClick(R.id.view_full_address_button)
    public void onViewFullAddressClicked() {
        ViewUtils.whenNonNull(getContext(), context -> {
            Dialog dialog = new AlertDialog.Builder(context, R.style.DefaultAlertDialog)
                    .setTitle(R.string.view_full_address_dialog_title)
                    .setMessage(address)
                    .setPositiveButton(R.string.ok, null)
                    .setNeutralButton(R.string.copy,
                            ((d, which) -> copyAddressToClipboard()))
                    .setCancelable(true)
                    .create();
            dialog.show();
            // You must show the dialog prior to retrieving the message text view.
            TextView message = dialog.findViewById(android.R.id.message);
            ViewUtils.whenNonNull(message, m -> {
                m.setTypeface(ResourcesCompat.getFont(context, R.font.string_literal));
            });
        });
    }

    private void copyAddressToClipboard() {
        Context context = getContext();

        String toastMessage;
        if (context != null
                && ViewUtils.copyToClipboard(getContext(),
                getString(R.string.address_clipboard_label),
                String.valueOf(address))) {
            toastMessage = getString(R.string.address_copied_success_toast);
        } else {
            toastMessage = getString(R.string.address_copied_failed_toast);
        }

        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }
}
