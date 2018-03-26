package com.ximcoin.ximwallet.presenter.contacts;

import android.arch.persistence.room.EmptyResultSetException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.persistence.contacts.Contact;
import com.ximcoin.ximwallet.model.repository.ContactRepository;
import com.ximcoin.ximwallet.presenter.common.BasePresenter;
import timber.log.Timber;

public class ViewContactPresenter extends BasePresenter<ViewContactContract.ViewContactView>
        implements ViewContactContract.ViewContactPresenter {
    @NonNull
    private final ContactRepository contactRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @Nullable
    private Contact contact;

    ViewContactPresenter(@NonNull ViewContactContract.ViewContactView view,
                                @NonNull ContactRepository contactRepository,
                                @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.contactRepository = contactRepository;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void resume() {
        super.resume();
        Long contactId = view.getContactId();

        if (contactId == null) {
            Timber.e("Contact id was null");
            view.showContactNotFound();
            return;
        }

        contactRepository.getContactById(contactId)
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> view.showLoading(false))
                .subscribe(c -> {
                    contact = c;
                    view.showContact(contact);
                }, throwable -> {
                    if (throwable instanceof EmptyResultSetException) {
                        view.showContactNotFound();
                    }

                    Timber.e(throwable, "Failed to load contact");
                });
    }

    @Override
    public void onUserRequestEdit() {
        if (contact != null) {
            view.goToEditContact(contact);
        }
    }

    @Override
    public void onUserRequestDelete() {
        if (contact != null) {
            view.showDeleteConfirmation();
        }
    }

    @Override
    public void onUserConfirmDelete() {
        if (contact == null) {
            return;
        }

        contactRepository.deleteContact(contact)
                .compose(schedulerProvider.completableScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> view.showLoading(false))
                .subscribe(view::goBack,
                        throwable -> {
                            Timber.e(throwable, "Failed to delete contact");
                            view.showDeleteFailed();
                        });
    }
}
