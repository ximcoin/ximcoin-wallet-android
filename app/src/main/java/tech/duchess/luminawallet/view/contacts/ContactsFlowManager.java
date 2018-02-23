package tech.duchess.luminawallet.view.contacts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.functions.Action;
import tech.duchess.luminawallet.model.persistence.contacts.Contact;

public interface ContactsFlowManager {
    void goBack();
    void onContactSelected(long contactId);
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
