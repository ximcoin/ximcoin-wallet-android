package tech.duchess.luminawallet.view.account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.view.util.ViewBindingUtils;

public class NoAccountFoundFragment extends RxFragment {
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.no_account_found_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.create_account_button)
    public void onCreateAccountClicked() {
        startCreateAccountFlow(false);
    }

    @OnClick(R.id.import_account_button)
    public void onImportAccountClicked() {
        startCreateAccountFlow(true);
    }

    private void startCreateAccountFlow(boolean isImportingSeed) {
        ViewBindingUtils.whenNonNull(getActivity(),
                activity -> ((IAccountsView) activity).startCreateAccountActivity(isImportingSeed));
    }
}
