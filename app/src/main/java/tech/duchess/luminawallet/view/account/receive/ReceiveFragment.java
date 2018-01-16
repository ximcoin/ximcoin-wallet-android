package tech.duchess.luminawallet.view.account.receive;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxFragment;

import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.view.account.IAccountPerspectiveView;

public class ReceiveFragment extends RxFragment implements IAccountPerspectiveView {
    private static final String ACCOUNT_KEY = "ReceiveFragment.ACCOUNT_KEY";

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
        return inflater.inflate(R.layout.temp_layout, container, false);
    }

    @Override
    public void setAccount(@Nullable Account account) {

    }
}
