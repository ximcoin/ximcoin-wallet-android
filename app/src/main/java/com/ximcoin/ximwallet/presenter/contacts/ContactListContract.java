package com.ximcoin.ximwallet.presenter.contacts;

import android.support.annotation.NonNull;

import java.util.List;

import com.ximcoin.ximwallet.model.persistence.contacts.Contact;
import com.ximcoin.ximwallet.presenter.common.Presenter;

public interface ContactListContract {
    interface ContactListView {
        void showLoading(boolean isLoading);
        void showContacts(@NonNull List<Contact> contacts);
        void goToAddNewContact();
        void propagateContactSelection(@NonNull Contact contact);
        void showLoadFailedError();
    }

    interface ContactListPresenter extends Presenter {
        void onUserRequestAddContact();
        void onUserSelectedContact(@NonNull Contact contact);
    }
}
