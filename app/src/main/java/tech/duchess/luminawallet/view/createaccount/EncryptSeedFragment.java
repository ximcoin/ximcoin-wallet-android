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

import com.trello.rxlifecycle2.components.support.RxFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import tech.duchess.luminawallet.LuminaWalletApp;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.dagger.module.FragmentLifecycleModule;
import tech.duchess.luminawallet.presenter.createaccount.EncryptSeedPresenter;
import tech.duchess.luminawallet.view.util.TextUtils;
import tech.duchess.luminawallet.view.util.ViewBindingUtils;

public class EncryptSeedFragment extends RxFragment implements IEncryptSeedView {
    private static final String SEED_KEY = "EncryptSeedFragment.SEED_KEY";

    @BindView(R.id.encryption_message)
    TextView encryptionMessage;

    @BindView(R.id.initial_password_field_layout)
    TextInputLayout primaryPasswordLayout;

    @BindView(R.id.verify_password_field_layout)
    TextInputLayout confirmationPasswordLayout;

    @BindView(R.id.btn_finish)
    Button finishButton;

    @Inject
    EncryptSeedPresenter presenter;

    private Unbinder unbinder;

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
        View view = inflater.inflate(R.layout.encrypt_seed_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        initMessage();

        LuminaWalletApp.getInstance()
                .getAppComponent()
                .plus(new FragmentLifecycleModule(this))
                .inject(this);

        presenter.attachView(this);

        return view;
    }

    private void initMessage() {
        ViewBindingUtils.whenNonNull(getContext(), c ->
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
        ViewBindingUtils.whenNonNull(getActivity(), activity ->
                ((ICreateAccountFlowManager) activity)
                        .setTitle(getString(R.string.encrypt_seed_fragment_title)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        presenter.detachView();
    }

    @Override
    public void showLoading(boolean isLoading) {
        ViewBindingUtils.whenNonNull(getActivity(), activity ->
                ((ICreateAccountFlowManager) activity).showLoading(isLoading));
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
        presenter.onUserFinished();
    }

    @Override
    public void finish() {
        ViewBindingUtils.whenNonNull(getActivity(),
                activity -> ((ICreateAccountFlowManager) activity).onAccountCreationCompleted());
    }

    @Nullable
    @Override
    public String getPrimaryFieldContents() {
        EditText editText = primaryPasswordLayout.getEditText();
        return editText == null ? null : editText.getText().toString();
    }

    @Nullable
    @Override
    public String getSecondaryFieldContents() {
        EditText editText = confirmationPasswordLayout.getEditText();
        return editText == null ? null : editText.getText().toString();
    }

    @Nullable
    @Override
    public String getSeed() {
        Bundle args = getArguments();

        return args == null ? null : args.getString(SEED_KEY);
    }
}
