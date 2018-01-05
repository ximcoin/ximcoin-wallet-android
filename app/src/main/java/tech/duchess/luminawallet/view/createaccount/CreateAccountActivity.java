package tech.duchess.luminawallet.view.createaccount;

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

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private String seed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            // First activity instance, need to initialize a fragment.
            if (TextUtils.isEmpty(seed)) {
                startSeedFragment();
            } else {
                startPasswordFragment();
            }
        }
    }

    private void startSeedFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new GenerateSeedFragment())
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void startPasswordFragment() {
        /*getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new GenerateSeedFragment())
                .commit();
        getSupportFragmentManager().executePendingTransactions();*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEED_KEY, seed);
    }

    @Override
    public void onSeedCreated(@NonNull String seed) {
        this.seed = seed;
        startPasswordFragment();
    }

    @Override
    public void onAccountCreationCompleted() {

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
