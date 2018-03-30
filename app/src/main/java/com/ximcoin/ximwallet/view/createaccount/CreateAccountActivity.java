package com.ximcoin.ximwallet.view.createaccount;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.ximcoin.ximwallet.R;
import com.ximcoin.ximwallet.view.common.BaseActivity;
import com.ximcoin.ximwallet.view.common.ProgressOverlay;
import com.ximcoin.ximwallet.view.util.ViewUtils;

public class CreateAccountActivity extends BaseActivity implements CreateAccountFlowManager,
        AccountSourceReceiver {
    private static final String IS_NEW_TO_LUMINA_KEY = "CreateAccountActivity.IS_NEW_TO_LUMINA_KEY";
    private static final String IS_IMPORTING_SEED_KEY = "CreateAccountActivity.IS_IMPORTING_SEED_KEY";
    private static final String IMPORTED_SEED_KEY = "CreateAccountActivity.IMPORTED_SEED_KEY";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.progress_overlay)
    ProgressOverlay progressOverlay;

    /**
     * Intent for when a user has already chosen if they want to create a new wallet or import a
     * pre-existing seed.
     */
    public static Intent createIntentForSeedGeneration(Context context, boolean isImportingSeed) {
        Intent intent = new Intent(context, CreateAccountActivity.class);
        intent.putExtra(IS_IMPORTING_SEED_KEY, isImportingSeed);
        return intent;
    }

    public static Intent createIntentForAccountSource(Context context, boolean isNewToLumina) {
        Intent intent = new Intent(context, CreateAccountActivity.class);
        intent.putExtra(IS_NEW_TO_LUMINA_KEY, isNewToLumina);
        return intent;
    }

    public static Intent createIntentForImportedSeed(Context context, @NonNull String seed) {
        Intent intent = new Intent(context, CreateAccountActivity.class);
        intent.putExtra(IMPORTED_SEED_KEY, seed);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();

        if (savedInstanceState == null) {
            if (didStartWithImportedSeed()) {
                startSeedCreationFragment(getIntent().getStringExtra(IMPORTED_SEED_KEY));
            } else if (didStartWithAccountSourceFragment()) {
                startSourceFragment(extras != null
                        && extras.getBoolean(IS_NEW_TO_LUMINA_KEY));
            } else {
                // didStartWithAccountSourceFragment() checks extras nullability
                // noinspection ConstantConditions
                startSeedCreationFragment(extras.getBoolean(IS_IMPORTING_SEED_KEY));
            }
        }

        // Proactively set the result to CANCELED, in case we back out of the activity.
        setResult(Activity.RESULT_CANCELED);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Not sure why, but the back button goes to previous activity when on first fragment,
        // however, 'home up' does not.
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (didStartWithImportedSeed() && isCurrentFragmentGenerateSeed()) {
            // Don't let user leave, as we've funded this particular seed.
            return;
        } else if (didStartWithImportedSeed() && isCurrentFragmentEncryptSeed()) {
            // We're backing out to generate seed fragment, remove home up.
            showHomeUp(false);
        }

        super.onBackPressed();
    }

    private void startSourceFragment(boolean isNewToLumina) {
        replaceFragment(R.id.fragment_container,
                AddAccountSourceFragment.getInstance(isNewToLumina), true);
    }

    private void startSeedCreationFragment(boolean isImportingSeed) {
        if (isImportingSeed) {
            startImportFragment();
        } else {
            startSeedFragment();
        }
    }

    private void startSeedCreationFragment(String importedSeed) {
        showHomeUp(false);
        replaceFragment(R.id.fragment_container,
                GenerateSeedFragment.getFragmentForPreSeed(getIntent().getExtras()
                        .getString(IMPORTED_SEED_KEY)),
                true);
    }

    private void startSeedFragment() {
        showHomeUp(true);
        if (didStartWithAccountSourceFragment()) {
            replaceFragment(R.id.fragment_container, new GenerateSeedFragment(), true,
                    GenerateSeedFragment.class.getSimpleName());
        } else {
            replaceFragment(R.id.fragment_container, new GenerateSeedFragment(), true);
        }
    }

    private void startImportFragment() {
        showHomeUp(true);
        if (didStartWithAccountSourceFragment()) {
            replaceFragment(R.id.fragment_container, new ImportSeedFragment(), true,
                    ImportSeedFragment.class.getSimpleName());
        } else {
            replaceFragment(R.id.fragment_container, new ImportSeedFragment(), true);
        }
    }

    private void startSeedEncryptionFragment(String seed) {
        showHomeUp(true);
        replaceFragment(R.id.fragment_container, EncryptSeedFragment.newInstance(seed), true,
                EncryptSeedFragment.class.getSimpleName());
    }

    @Override
    public void onSeedCreated(@NonNull String seed) {
        startSeedEncryptionFragment(seed);
    }

    @Override
    public void onAccountCreationCompleted() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setTitle(@NonNull String title) {
        super.setTitle(title);
    }

    private boolean didStartWithImportedSeed() {
        Bundle extras = getIntent().getExtras();
        return extras != null && extras.containsKey(IMPORTED_SEED_KEY);
    }

    private boolean didStartWithAccountSourceFragment() {
        Bundle extras = getIntent().getExtras();
        return extras == null || extras.containsKey(IS_NEW_TO_LUMINA_KEY);
    }

    @Override
    public void onUserRequestedAccountCreation(boolean isImportingSeed) {
        startSeedCreationFragment(isImportingSeed);
    }

    @Override
    public void showBlockedLoading(@Nullable String message) {
        progressOverlay.show(message);
    }

    @Override
    public void hideBlockedLoading() {
        progressOverlay.hide(null, true, true);
    }

    private void showHomeUp(boolean shouldShow) {
        ViewUtils.whenNonNull(getSupportActionBar(), actionBar -> {
            actionBar.setDisplayHomeAsUpEnabled(shouldShow);
            actionBar.setHomeButtonEnabled(shouldShow);
        });
    }

    private boolean isCurrentFragmentGenerateSeed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        return fragment != null && fragment instanceof GenerateSeedFragment;
    }

    private boolean isCurrentFragmentEncryptSeed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        return fragment != null && fragment instanceof EncryptSeedFragment;
    }
}
