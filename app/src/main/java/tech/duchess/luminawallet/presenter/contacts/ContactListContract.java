package tech.duchess.luminawallet.presenter.contacts;

import android.support.annotation.NonNull;

import java.util.List;

import tech.duchess.luminawallet.model.persistence.contacts.Contact;
import tech.duchess.luminawallet.presenter.common.Presenter;

public interface ContactListContract {
    interface ContactListView {
        void showLoading(boolean isLoading);
        void showContacts(@NonNull List<Contact> contacts);
        void goToAddNewContact();
        void goToViewContact(@NonNull Contact contact);
        void showLoadFailedError();
    }

    interface ContactListPresenter extends Presenter {
        void onUserRequestAddContact();
        void onUserSelectedContact(@NonNull Contact contact);
    }
}
