package tech.duchess.luminawallet.view.createaccount;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.presenter.createaccount.EncryptSeedContract;
import tech.duchess.luminawallet.view.common.BaseViewFragment;
import tech.duchess.luminawallet.view.util.TextUtils;
import tech.duchess.luminawallet.view.util.ViewUtils;

public class EncryptSeedFragment extends BaseViewFragment<EncryptSeedContract.EncryptSeedPresenter>
        implements EncryptSeedContract.EncryptSeedView {
    private static final String SEED_KEY = "EncryptSeedFragment.SEED_KEY";

    @BindView(R.id.encryption_message)
    TextView encryptionMessage;

    @BindView(R.id.initial_password_field_layout)
    TextInputLayout primaryPasswordLayout;

    @BindView(R.id.verify_password_field_layout)
    TextInputLayout confirmationPasswordLayout;

    @BindView(R.id.btn_finish)
    Button finishButton;

    public static EncryptSeedFragment newInstance(@NonNull String seed) {
        EncryptSeedFragment encryptSeedFragment = new EncryptSeedFragment();
        Bundle args = new Bundle();
        args.putString(SEED_KEY, seed);
        encryptSeedFragment.setArguments(args);
        return encryptSeedFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.encrypt_seed_fragment, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        initMessage();
    }

    private void initMessage() {
        ViewUtils.whenNonNull(getContext(), c ->
                encryptionMessage.setText(TextUtils.getBulletedList(
                        5,
                        null,
                        c,
                        R.string.encryption_message_bullet1,
                        R.string.encryption_message_bullet2,
                        R.string.encryption_message_bullet3,
                        R.string.encryption_message_bullet4,
                        R.string.encryption_message_bullet5,
                        R.string.encryption_message_bullet6)));
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewUtils.whenNonNull(getActivity(), activity ->
                ((CreateAccountFlowManager) activity)
                        .setTitle(getString(R.string.encrypt_seed_fragment_title)));
    }

    @Override
    public void showLoading(boolean isLoading) {
        ViewUtils.whenNonNull(getActivity(), activity ->
                ((CreateAccountFlowManager) activity).showLoading(isLoading));
    }

    @OnTextChanged({R.id.initial_password_field, R.id.verify_password_field})
    public void onTextChanged(Editable editable) {
        presenter.onPasswordContentsChanged();
    }

    @Override
    public void showPasswordLengthError() {
        primaryPasswordLayout.setError(getString(R.string.password_length_error));
    }

    @Override
    public void hidePasswordLengthError() {
        primaryPasswordLayout.setError(null);
    }

    @Override
    public void showPasswordMismatchError() {
        confirmationPasswordLayout.setError(getString(R.string.passwords_match_error));
    }

    @Override
    public void hidePasswordMismatchError() {
        confirmationPasswordLayout.setError(null);
    }

    @Override
    public void setFinishEnabled(boolean isEnabled) {
        finishButton.setEnabled(isEnabled);
    }

    @Override
    public void showSomethingWrongError() {

    }

    @OnClick(R.id.btn_finish)
    public void onUserClickFinish() {
        presenter.onUserFinished(
                getFieldContents(primaryPasswordLayout),
                getFieldContents(confirmationPasswordLayout),
                getSeed());
    }

    @Override
    public void finish() {
        ViewUtils.whenNonNull(getActivity(),
                activity -> ((CreateAccountFlowManager) activity).onAccountCreationCompleted());
    }

    @Nullable
    private String getFieldContents(@NonNull TextInputLayout layout) {
        EditText editText = layout.getEditText();
        return editText == null ? null : editText.getText().toString();
    }

    @Nullable
    private String getSeed() {
        Bundle args = getArguments();
        return args == null ? null : args.getString(SEED_KEY);
    }
}
