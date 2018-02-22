package tech.duchess.luminawallet.view.contacts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.contacts.Contact;
import tech.duchess.luminawallet.view.common.BaseActivity;
import tech.duchess.luminawallet.view.util.ViewUtils;

public class ContactsActivity extends BaseActivity implements ContactsFlowManager {
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

        if (savedInstanceState == null) {
            startContactListFragment();
        }
    }

    private void startContactListFragment() {
        replaceFragment(R.id.fragment_container, new ContactListFragment(), true);
    }

    private void startViewContactFragment(long contactId) {
        replaceFragment(R.id.fragment_container, ViewContactFragment.getInstance(contactId),
                true, ViewContactFragment.class.getSimpleName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContactSelected(long contactId) {
        startViewContactFragment(contactId);
    }

    @Override
    public void onAddContactRequested() {
        Toast.makeText(this, "Add contact requested", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEditContactRequested(@NonNull Contact contact) {

    }

    @Override
    public void setTitle(@NonNull String title) {
        super.setTitle(title);
    }
}
