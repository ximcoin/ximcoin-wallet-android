package tech.duchess.luminawallet.presenter.contacts;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.persistence.contacts.Contact;
import tech.duchess.luminawallet.model.repository.ContactRepository;
import tech.duchess.luminawallet.presenter.common.BasePresenter;
import timber.log.Timber;

public class ContactListPresenter extends BasePresenter<ContactListContract.ContactListView>
        implements ContactListContract.ContactListPresenter {
    @NonNull
    private final ContactRepository contactRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    ContactListPresenter(@NonNull ContactListContract.ContactListView view,
                         @NonNull ContactRepository contactRepository,
                         @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.contactRepository = contactRepository;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void resume() {
        super.resume();
        refreshContacts();
    }

    private void refreshContacts() {
        contactRepository.getAllContacts()
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> view.showLoading(false))
                .subscribe(view::showContacts,
                        throwable -> {
                            Timber.e(throwable, "Failed to load contacts");
                            view.showLoadFailedError();
                        });
    }

    @Override
    public void onUserRequestAddContact() {
        view.goToAddNewContact();
    }

    @Override
    public void onUserSelectedContact(@NonNull Contact contact) {
        view.goToViewContact(contact);
    }
}
