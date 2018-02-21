package tech.duchess.luminawallet.view.contacts;

import android.support.annotation.NonNull;

import tech.duchess.luminawallet.model.persistence.contacts.Contact;

public interface ContactsFlowManager {
    void onContactSelected(@NonNull Contact contact);
    void onAddContactRequested();
}
