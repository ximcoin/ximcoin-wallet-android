package com.ximcoin.ximwallet.presenter.contacts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ximcoin.ximwallet.model.persistence.contacts.Contact;
import com.ximcoin.ximwallet.presenter.common.Presenter;

public interface ViewContactContract {
    interface ViewContactView {
        @Nullable
        Long getContactId();

        void showContact(@NonNull Contact contact);

        void showContactNotFound();

        void goBack();

        void showLoading(boolean isLoading);

        void goToEditContact(@NonNull Contact contact);

        void showDeleteFailed();

        void showDeleteConfirmation();
    }

    interface ViewContactPresenter extends Presenter {
        void onUserRequestEdit();

        void onUserRequestDelete();

        void onUserConfirmDelete();
    }
}
