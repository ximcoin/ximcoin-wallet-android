package tech.duchess.luminawallet.view.inflation;

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
import io.reactivex.functions.Action;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.view.common.BaseActivity;
import tech.duchess.luminawallet.view.common.ProgressOverlay;
import tech.duchess.luminawallet.view.util.ViewUtils;

public class InflationActivity extends BaseActivity implements InflationFlowManager {
    private static final String ACCOUNT_ID_KEY = "InflationActivity.ACCOUNT_ID_KEY";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.progress_overlay)
    ProgressOverlay progressOverlay;

    public static Intent createIntent(@NonNull Context context, @NonNull String accountId) {
        Intent intent = new Intent(context, InflationActivity.class);
        intent.putExtra(ACCOUNT_ID_KEY, accountId);
        return intent;
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

        if (savedInstanceState == null) {
            startInflationFragment();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startInflationFragment() {
        replaceFragment(R.id.fragment_container,
                InflationFragment.getInstance(getIntent().getStringExtra(ACCOUNT_ID_KEY)),
                true);
    }

    @Override
    public void onInflationSetComplete() {
        finish();
    }

    @Override
    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showBlockedLoading(@Nullable String message) {
        progressOverlay.show(message);
    }

    @Override
    public void hideBlockedLoading(@Nullable String message,
                                   boolean wasSuccess,
                                   boolean immediate,
                                   @Nullable Action action) {
        progressOverlay.hide(message, wasSuccess, immediate, action);
    }

    @Override
    public void setTitle(@NonNull String title) {
        toolbar.setTitle(title);
    }
}
