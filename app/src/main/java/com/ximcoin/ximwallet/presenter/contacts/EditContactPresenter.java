package com.ximcoin.ximwallet.presenter.contacts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.persistence.contacts.Contact;
import com.ximcoin.ximwallet.model.repository.ContactRepository;
import com.ximcoin.ximwallet.model.util.AccountUtil;
import com.ximcoin.ximwallet.presenter.common.BasePresenter;
import timber.log.Timber;

public class EditContactPresenter extends BasePresenter<EditContactContract.EditContactView>
        implements EditContactContract.EditContactPresenter {

    @NonNull
    private final ContactRepository contactRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    private Contact originalContact;

    EditContactPresenter(@NonNull EditContactContract.EditContactView view,
                         @NonNull ContactRepository contactRepository,
                         @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.contactRepository = contactRepository;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void start(@Nullable Bundle bundle) {
        super.start(bundle);
        originalContact = view.getContact();

        if (originalContact == null) {
            // Creating a new contact
            originalContact = new Contact();
        }
        
        if (bundle == null) {
            // First start and we're editing a contact. Populate fields with initial values.
            view.displayContactContents(originalContact);
        }
    }

    @Override
    public void onUserRequestGoBack(@Nullable String name,
                                    @Nullable String address,
                                    @Nullable String notes,
                                    int colorIndex) {
        if (hasPendingChanges(name, address, notes, colorIndex)) {
            view.showDiscardChangesConfirmation();
        } else {
            view.goBack();
        }
    }

    @Override
    public void onUserRequestSave(@Nullable String name,
                                  @Nullable String address,
                                  @Nullable String notes,
                                  int colorIndex) {
        if (checkFieldErrors(name, address)) {
            return;
        }

        if (!hasPendingChanges(name, address, notes, colorIndex)) {
            view.goBack();
        } else {
            onUserRequestSaveInternal(name, address, notes, colorIndex);
        }
    }

    @Override
    public void onUserConfirmedDiscardChanges() {
        view.goBack();
    }

    private boolean checkFieldErrors(@Nullable String name,
                                     @Nullable String address) {
        EditContactError error = null;

        if (name == null || name.isEmpty()) {
            error = EditContactError.NAME_EMPTY;
        } else if (!AccountUtil.publicKeyOfProperLength(address)) {
            error = EditContactError.ADDRESS_LENGTH;
        } else if (!AccountUtil.publicKeyOfProperPrefix(address)) {
            error = EditContactError.ADDRESS_PREFIX;
        } else if (!AccountUtil.publicKeyCanBeDecoded(address)) {
            error = EditContactError.ADDRESS_FORMAT;
        }

        if (error != null) {
            view.showError(error);
            return true;
        } else {
            return false;
        }
    }

    private void onUserRequestSaveInternal(@Nullable String name,
                                           @Nullable String address,
                                           @Nullable String notes,
                                           int colorIndex) {
        Contact editedContact = originalContact.deepCopy();
        editedContact.setName(name);
        editedContact.setAddress(address);
        editedContact.setNotes(notes);
        editedContact.setColorIndex(colorIndex);

        contactRepository.addOrUpdate(editedContact)
                .compose(schedulerProvider.completableScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showBlockedLoading();
                })
                .subscribe(() -> view.hideBlockedLoading(true, true),
                        throwable -> {
                            Timber.e(throwable, "Failed to save contact");
                            view.hideBlockedLoading(false, false);
                        });
    }

    private boolean hasPendingChanges(@Nullable String name,
                                      @Nullable String address,
                                      @Nullable String memo,
                                      int colorIndex) {
        return !isFieldEqual(name, originalContact.getName())
                || !isFieldEqual(address, originalContact.getAddress())
                || !isFieldEqual(memo, originalContact.getNotes())
                || colorIndex != originalContact.getColorIndex();
    }

    private boolean isFieldEqual(@Nullable String field1,
                                 @Nullable String field2) {
        if (field1 == null) {
            return field2 == null || field2.isEmpty();
        } else {
            return field1.equals(field2) || (field1.isEmpty() && (field2 == null || field2.isEmpty()));
        }
    }
}
