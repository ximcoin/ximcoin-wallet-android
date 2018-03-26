package com.ximcoin.ximwallet.view.createaccount;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.stellar.sdk.KeyPair;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.ximcoin.ximwallet.R;
import com.ximcoin.ximwallet.view.util.TextUtils;
import com.ximcoin.ximwallet.view.util.ViewUtils;

public class GenerateSeedFragment extends Fragment {
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

    @BindView(R.id.scroll_container)
    ScrollView scrollView;

    @BindViews({R.id.btn_next, R.id.btn_copy_seed, R.id.btn_generate_new_seed, R.id.seed})
    List<View> postViewSeedViews;

    private Unbinder unbinder;

    private boolean didShowSeed = false;

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
        }

        if (didShowSeed) {
            // We've already displayed the seed before. Keep it visible as to not make the user
            // think a new seed may have generated.
            showSeed();
            scrollToBottom();
        }

        setSeedWarningMessage();
        setSeedText();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewUtils.whenNonNull(getActivity(), activity ->
                ((CreateAccountFlowManager) activity)
                        .setTitle(getString(R.string.generate_seed_fragment_title)));
    }

    private void setSeedWarningMessage() {
        ViewUtils.whenNonNull(getContext(), c ->
                warningMessage.setText(TextUtils.getBulletedList(
                        5,
                        null,
                        c,
                        R.string.seed_warning_bullet1,
                        R.string.seed_warning_bullet2_generate,
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
        Context context = getContext();

        String toastMessage;
        if (context != null
                && ViewUtils.copyToClipboard(getContext(),
                getString(R.string.seed_clipboard_label),
                String.valueOf(keyPair.getSecretSeed()))) {
            toastMessage = getString(R.string.seed_copied_success_toast);
        } else {
            toastMessage = getString(R.string.seed_copy_failed_toast);
        }

        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_generate_new_seed)
    public void generateNewSeed() {
        keyPair = KeyPair.random();
        setSeedText();
    }

    @OnClick(R.id.btn_next)
    public void goNext() {
        ViewUtils.whenNonNull(getActivity(),
                activity -> ((CreateAccountFlowManager) activity)
                        .onSeedCreated(String.valueOf(keyPair.getSecretSeed())));
    }

    @OnClick(R.id.btn_show_seed)
    public void showSeed() {
        didShowSeed = true;
        showSeedButton.setVisibility(View.GONE);
        ButterKnife.apply(postViewSeedViews, (ButterKnife.Action<? super View>)
                (view, index) -> view.setVisibility(View.VISIBLE));
        scrollToBottom();
    }

    private void scrollToBottom() {
        scrollView.postDelayed(() ->
                scrollView.fullScroll(ScrollView.FOCUS_DOWN), 100);
    }
}
