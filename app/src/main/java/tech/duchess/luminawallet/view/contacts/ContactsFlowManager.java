package tech.duchess.luminawallet.view.contacts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.functions.Action;
import tech.duchess.luminawallet.model.persistence.contacts.Contact;

public interface ContactsFlowManager {
    String CONTACT_SELECTION_RESULT_KEY = "ContactsActivity.CONTACT_SELECTION_RESULT_KEY";

    void goBack();
    void onContactSelected(@NonNull Contact contact);
    void onAddContactRequested();
    void showLoading(boolean isLoading);
    void onEditContactRequested(@NonNull Contact contact);
    void setTitle(@NonNull String title);
    void showBlockedLoading(@Nullable String message);
    void hideBlockedLoading(@Nullable String message,
                            boolean wasSuccess,
                            boolean immediate,
                            @Nullable Action action);
}
