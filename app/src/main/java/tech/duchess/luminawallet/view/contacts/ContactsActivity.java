package tech.duchess.luminawallet.view.contacts;

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
import io.reactivex.functions.Action;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.contacts.Contact;
import tech.duchess.luminawallet.view.common.BackPressListener;
import tech.duchess.luminawallet.view.common.BaseActivity;
import tech.duchess.luminawallet.view.common.ProgressOverlay;
import tech.duchess.luminawallet.view.util.ViewUtils;

public class ContactsActivity extends BaseActivity implements ContactsFlowManager {
    private static final String IS_SELECTING_CONTACT_KEY = "ContactsActivity.IS_SELECTING_CONTACT_KEY";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.progress_overlay)
    ProgressOverlay progressOverlay;

    private boolean isSelectingContact = false;

    public static Intent createIntentForContactSelection(@NonNull Context context) {
        Intent intent = new Intent(context, ContactsActivity.class);
        intent.putExtra(IS_SELECTING_CONTACT_KEY, true);
        return intent;
    }

    public static Intent createIntentForViewContacts(@NonNull Context context) {
        Intent intent = new Intent(context, ContactsActivity.class);
        intent.putExtra(IS_SELECTING_CONTACT_KEY, false);
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
            startContactListFragment();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean(IS_SELECTING_CONTACT_KEY, false)) {
            isSelectingContact = true;
            // Proactively set result to canceled in case we back out of the activity without
            // making a selection.
            setResult(RESULT_CANCELED);
        }
    }

    private void startContactListFragment() {
        replaceFragment(R.id.fragment_container, new ContactListFragment(), true);
    }

    private void startViewContactFragment(long contactId) {
        replaceFragment(R.id.fragment_container, ViewContactFragment.getInstance(contactId),
                true, ViewContactFragment.class.getSimpleName());
    }

    private void startEditContactFragment(@Nullable Contact contact) {
        replaceFragment(R.id.fragment_container, EditContactFragment.getInstance(contact),
                true, EditContactFragment.class.getSimpleName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!checkFragmentConsumedBackPress()) {
                goBack();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!checkFragmentConsumedBackPress()) {
            goBack();
        }
    }

    @Override
    public void goBack() {
        super.onBackPressed();
    }

    @Override
    public void onContactSelected(@NonNull Contact contact) {
        if (isSelectingContact) {
            Intent returnData = new Intent();
            returnData.putExtra(CONTACT_SELECTION_RESULT_KEY, contact);
            setResult(RESULT_OK, returnData);
            finish();
        } else {
            startViewContactFragment(contact.getId());
        }
    }

    @Override
    public void onAddContactRequested() {
        startEditContactFragment(null);
    }

    @Override
    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEditContactRequested(@NonNull Contact contact) {
        startEditContactFragment(contact);
    }

    @Override
    public void setTitle(@NonNull String title) {
        super.setTitle(title);
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

    boolean checkFragmentConsumedBackPress() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment instanceof BackPressListener) {
            ((BackPressListener) fragment).onBackPressed();
            return true;
        } else {
            return false;
        }
    }
}
