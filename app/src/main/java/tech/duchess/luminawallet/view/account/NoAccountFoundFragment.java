package tech.duchess.luminawallet.view.account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.view.util.ViewUtils;

public class NoAccountFoundFragment extends Fragment {
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
        ViewUtils.whenNonNull(getActivity(), activity ->
                ((AccountsActivity) activity).onUserRequestedAccountCreation(isImportingSeed));
    }
}
