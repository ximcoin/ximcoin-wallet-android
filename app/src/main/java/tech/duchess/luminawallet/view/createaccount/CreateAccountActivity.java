package tech.duchess.luminawallet.view.createaccount;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.view.common.BaseActivity;
import tech.duchess.luminawallet.view.util.ViewUtils;

public class CreateAccountActivity extends BaseActivity implements CreateAccountFlowManager,
        AccountSourceReceiver {
    private static final String IS_NEW_TO_LUMINA_KEY = "CreateAccountActivity.IS_NEW_TO_LUMINA_KEY";
    private static final String IS_IMPORTING_SEED_KEY = "CreateAccountActivity.IS_IMPORTING_SEED_KEY";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

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
        return new Intent(context, CreateAccountActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ViewUtils.whenNonNull(getSupportActionBar(), actionBar -> {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        });

        Bundle extras = getIntent().getExtras();

        if (savedInstanceState == null) {
            if (didStartWithAccountSourceFragment()) {
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

    private void startSeedFragment() {
        if (didStartWithAccountSourceFragment()) {
            replaceFragment(R.id.fragment_container, new GenerateSeedFragment(), true,
                    GenerateSeedFragment.class.getSimpleName());
        } else {
            replaceFragment(R.id.fragment_container, new GenerateSeedFragment(), true);
        }
    }

    private void startImportFragment() {
        if (didStartWithAccountSourceFragment()) {
            replaceFragment(R.id.fragment_container, new ImportSeedFragment(), true,
                    ImportSeedFragment.class.getSimpleName());
        } else {
            replaceFragment(R.id.fragment_container, new ImportSeedFragment(), true);
        }
    }

    private void startSeedEncryptionFragment(String seed) {
        replaceFragment(R.id.fragment_container, EncryptSeedFragment.newInstance(seed), true,
                EncryptSeedFragment.class.getSimpleName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

    private boolean didStartWithAccountSourceFragment() {
        Bundle extras = getIntent().getExtras();
        return extras == null || extras.containsKey(IS_NEW_TO_LUMINA_KEY);
    }

    @Override
    public void onUserRequestedAccountCreation(boolean isImportingSeed) {
        startSeedCreationFragment(isImportingSeed);
    }
}
