package tech.duchess.luminawallet.view.contacts;

import android.support.annotation.NonNull;

import tech.duchess.luminawallet.model.persistence.contacts.Contact;

public interface ContactsFlowManager {
    void onContactSelected(long contactId);
    void onAddContactRequested();
    void showLoading(boolean isLoading);
    void onEditContactRequested(@NonNull Contact contact);
    void setTitle(@NonNull String title);
}
