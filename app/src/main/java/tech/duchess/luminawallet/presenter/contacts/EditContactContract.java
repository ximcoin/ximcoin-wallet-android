package tech.duchess.luminawallet.presenter.contacts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import tech.duchess.luminawallet.model.persistence.contacts.Contact;
import tech.duchess.luminawallet.presenter.common.Presenter;

public interface EditContactContract {

    interface EditContactView {
        @Nullable
        Contact getContact();

        void displayContactContents(@NonNull Contact contact);

        void showDiscardChangesConfirmation();

        void showBlockedLoading();

        void hideBlockedLoading(boolean wasSuccess, boolean shouldGoBack);

        void goBack();

        void showError(EditContactPresenter.EditContactError error);
    }

    interface EditContactPresenter extends Presenter {

        enum EditContactError {
            NAME_EMPTY,
            ADDRESS_LENGTH,
            ADDRESS_PREFIX,
            ADDRESS_FORMAT,
        }

        void onUserRequestGoBack(@Nullable String name,
                                 @Nullable String address,
                                 @Nullable String notes,
                                 int colorIndex);

        void onUserRequestSave(@Nullable String name,
                               @Nullable String address,
                               @Nullable String notes,
                               int colorIndex);

        void onUserConfirmedDiscardChanges();
    }
}
