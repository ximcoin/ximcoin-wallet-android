package tech.duchess.luminawallet.view.account.receive;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.trello.rxlifecycle2.components.support.RxFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.view.account.IAccountPerspectiveView;
import tech.duchess.luminawallet.view.util.ViewBindingUtils;

public class ReceiveFragment extends RxFragment implements IAccountPerspectiveView {
    private static final String ACCOUNT_KEY = "ReceiveFragment.ACCOUNT_KEY";

    @BindView(R.id.qr_code)
    ImageView qrCode;

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
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setAccount(@Nullable Account account) {
        qrCode.setImageBitmap(ViewBindingUtils.encodeAsBitmap(account.getAccount_id()));
    }
}
