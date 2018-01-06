package tech.duchess.luminawallet.view.createaccount;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxFragment;

import org.stellar.sdk.KeyPair;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.view.util.TextUtils;
import tech.duchess.luminawallet.view.util.ViewBindingUtils;

public class GenerateSeedFragment extends RxFragment {
    private static final String SEED_CLIP_LABEL = "StellarAccountSeed";
    private static final String SEED_KEY = "GenerateSeedFragment.SEED_KEY";
    private static final String SEED_SHOWN_KEY = "GeneratedSeedFragment.SEED_SHOWN_KEY";

    @NonNull
    KeyPair keyPair = KeyPair.random();

    @BindView(R.id.warning_message)
    TextView warningMessage;

    @BindView(R.id.seed)
    TextView seedView;

    @BindView(R.id.btn_show_seed)
    Button showSeedButton;

    @BindView(R.id.btn_next)
    Button nextButton;

    @BindViews({R.id.btn_next, R.id.btn_copy_seed, R.id.btn_generate_new_seed, R.id.seed})
    List<View> postViewSeedViews;

    Unbinder unbinder;

    private boolean didShowSeed = false;

    public static GenerateSeedFragment newInstance(@NonNull String seed) {
        Bundle args = new Bundle();
        args.putString(SEED_KEY, seed);
        GenerateSeedFragment generateSeedFragment = new GenerateSeedFragment();
        generateSeedFragment.setArguments(args);
        return generateSeedFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.generate_seed_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            String seed = savedInstanceState.getString(SEED_KEY);
            if (!TextUtils.isEmpty(seed)) {
                keyPair = KeyPair.fromSecretSeed(seed);
            }

            didShowSeed = savedInstanceState.getBoolean(SEED_SHOWN_KEY, false);
            if (didShowSeed) {
                showSeed();
            }
        } else {
            // First time start. Check arguments to see if we were given a seed.
            Bundle args = getArguments();
            if (args != null) {
                String seed = getArguments().getString(SEED_KEY);
                if (!TextUtils.isEmpty(seed)) {
                    keyPair = KeyPair.fromSecretSeed(seed);
                    showSeed();
                }
            }
        }

        setSeedWarningMessage();
        setSeedText();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewBindingUtils.whenNonNull(getActivity(), activity ->
                ((ICreateAccountFlowManager) activity)
                        .setTitle(getString(R.string.generate_seed_fragment_title)));
    }

    private void setSeedWarningMessage() {
        ViewBindingUtils.whenNonNull(getContext(), c ->
                warningMessage.setText(TextUtils.getBulletedList(
                        5,
                        null,
                        c,
                        R.string.seed_warning_bullet1,
                        R.string.seed_warning_bullet2,
                        R.string.seed_warning_bullet3,
                        R.string.seed_warning_bullet4)));
    }

    private void setSeedText() {
        seedView.setText(String.valueOf(keyPair.getSecretSeed()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEED_KEY, String.valueOf(keyPair.getSecretSeed()));
        outState.putBoolean(SEED_SHOWN_KEY, didShowSeed);
    }

    @OnClick(R.id.btn_copy_seed)
    public void copySeed() {
        Activity parentActivity = getActivity();
        if (parentActivity != null) {
            ClipboardManager clipboardManager =
                    (ClipboardManager) parentActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager != null) {
                clipboardManager.setPrimaryClip(ClipData.newPlainText(SEED_CLIP_LABEL,
                        String.valueOf(keyPair.getSecretSeed())));
            }
            Toast.makeText(parentActivity, getString(R.string.seed_copied_toast),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_generate_new_seed)
    public void generateNewSeed() {
        keyPair = KeyPair.random();
        setSeedText();
    }

    @OnClick(R.id.btn_next)
    public void goNext() {
        Activity parentActivity = getActivity();
        if (parentActivity != null) {
            ((ICreateAccountFlowManager) parentActivity)
                    .onSeedCreated(String.valueOf(keyPair.getSecretSeed()));
        }
    }

    @OnClick(R.id.btn_show_seed)
    public void showSeed() {
        didShowSeed = true;
        showSeedButton.setVisibility(View.GONE);
        ButterKnife.apply(postViewSeedViews, (ButterKnife.Action<? super View>)
                (view, index) -> view.setVisibility(View.VISIBLE));
    }
}
