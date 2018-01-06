package tech.duchess.luminawallet.view.createaccount;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.duchess.luminawallet.R;

public class CreateAccountActivity extends RxAppCompatActivity implements ICreateAccountFlowManager {
    private static final String SEED_KEY = "CreateAccountActivity.SEED_KEY";
    private static final String ON_SEED_GENERATION_FRAGMENT_KEY = "CreateAccountActivity.ON_SEED_GENERATION_FRAGMENT_KEY";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private String seed;
    private boolean onSeedGenerationFragment = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            // First activity instance, need to initialize a fragment. We do a check here in case
            // we're being started with the intent of importing an account via seed.
            if (TextUtils.isEmpty(seed)) {
                startSeedFragment();
            } else {
                startSeedEncryptionFragment();
            }
        } else {
            seed = savedInstanceState.getString(SEED_KEY, null);
            onSeedGenerationFragment =
                    savedInstanceState.getBoolean(ON_SEED_GENERATION_FRAGMENT_KEY, true);
        }
    }

    @Override
    public void onBackPressed() {
        if (!onSeedGenerationFragment) {
            // Must be on the seed encryption fragment. Just go back to the seed fragment.
            startSeedFragment();
        } else {
            // On the seed fragment. Just finish the activity.
            setResult(Activity.RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    private void startSeedFragment() {
        onSeedGenerationFragment = true;
        GenerateSeedFragment seedFragment = TextUtils.isEmpty(seed)
                ? new GenerateSeedFragment()
                : GenerateSeedFragment.newInstance(seed);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, seedFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void startSeedEncryptionFragment() {
        onSeedGenerationFragment = false;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, EncryptSeedFragment.newInstance(seed))
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEED_KEY, seed);
        outState.putBoolean(ON_SEED_GENERATION_FRAGMENT_KEY, onSeedGenerationFragment);
    }

    @Override
    public void onSeedCreated(@NonNull String seed) {
        this.seed = seed;
        startSeedEncryptionFragment();
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
}
