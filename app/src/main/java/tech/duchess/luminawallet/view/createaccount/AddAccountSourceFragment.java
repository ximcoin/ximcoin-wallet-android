package tech.duchess.luminawallet.view.createaccount;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.view.util.ViewUtils;

public class AddAccountSourceFragment extends Fragment {
    private static final String IS_NEW_TO_LUMINA_ARG = "AddAccountSourceFragment.IS_NEW_TO_LUMINA_ARG";

    @BindView(R.id.message)
    TextView message;

    private Unbinder unbinder;

    public static AddAccountSourceFragment getInstance(boolean isNewToLumina) {
        Bundle args = new Bundle();
        args.putBoolean(IS_NEW_TO_LUMINA_ARG, isNewToLumina);
        AddAccountSourceFragment sourceFragment = new AddAccountSourceFragment();
        sourceFragment.setArguments(args);
        return sourceFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_account_source_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initMessage();
        return view;
    }

    private void initMessage() {
        Bundle args = getArguments();
        boolean isNew = args != null && args.getBoolean(IS_NEW_TO_LUMINA_ARG, false);
        message.setText(isNew ? R.string.new_to_lumina_message : R.string.account_source_message);
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
                ((AccountSourceReceiver) activity).onUserRequestedAccountCreation(isImportingSeed));
    }
}
