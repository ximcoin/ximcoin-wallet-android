package tech.duchess.luminawallet.view.createaccount;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.view.common.BaseActivity;
import tech.duchess.luminawallet.view.util.ViewUtils;

public class CreateAccountActivity extends BaseActivity implements ICreateAccountFlowManager {
    private static final String SEED_KEY = "CreateAccountActivity.SEED_KEY";
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

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

        // Lock the drawer. Only one way out of this activity, and that's either canceling the
        // account creation or completing it. This prevents users from backing into this activity
        // (or expecting to be able to back into this activity) after having navigated elsewhere
        // throughout the application.
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        Bundle extras = getIntent().getExtras();
        String seed = extras == null ? null : extras.getString(SEED_KEY, null);

        if (savedInstanceState == null) {
            // First activity instance, need to initialize a fragment. We do a check here in case
            // we're being started with the intent of importing an account via seed.
            if (TextUtils.isEmpty(seed)) {
                startSeedFragment();
            } else {
                startSeedEncryptionFragment(true, seed);
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

    private void startSeedFragment() {
        replaceFragment(R.id.fragment_container, new GenerateSeedFragment(), true);
    }

    private void startSeedEncryptionFragment(boolean isFirstFragment, String seed) {
        Fragment fragment = EncryptSeedFragment.newInstance(seed);
        if (isFirstFragment) {
            replaceFragment(R.id.fragment_container, fragment, true,
                    EncryptSeedFragment.class.getSimpleName());
        } else {
            replaceFragment(R.id.fragment_container, fragment, true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSeedCreated(@NonNull String seed) {
        startSeedEncryptionFragment(false, seed);
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
