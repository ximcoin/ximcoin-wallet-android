package tech.duchess.luminawallet.model.repository;

import android.arch.persistence.room.EmptyResultSetException;
import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import tech.duchess.luminawallet.model.persistence.AppFlagDB;
import tech.duchess.luminawallet.model.persistence.ContactDB;
import tech.duchess.luminawallet.model.persistence.appflag.AppFlag;
import tech.duchess.luminawallet.model.persistence.appflag.AppFlagDao;
import tech.duchess.luminawallet.model.persistence.contacts.Contact;
import tech.duchess.luminawallet.model.persistence.contacts.ContactDao;
import timber.log.Timber;

public class ContactRepository {
    private static final Contact DONATION_CONTACT;

    @NonNull
    private final ContactDao contactDao;

    @NonNull
    private final AppFlagDao appFlagDao;

    static {
        DONATION_CONTACT = new Contact();
        DONATION_CONTACT.setName("Lumina Donation Address");
        DONATION_CONTACT.setAddress("GBA5ZORABPGPYTPUCD2M46LPMDTD43MZ5V2PRMKH7ZHZLRZJ6ULUMINA");
        DONATION_CONTACT.setNotes("Thanks for using Lumina!");
    }

    @Inject
    public ContactRepository(@NonNull ContactDB contactDB,
                             @NonNull AppFlagDB appFlagDB) {
        this.contactDao = contactDB.contactDao();
        this.appFlagDao = appFlagDB.appFlagDao();
    }

    @NonNull
    public Single<List<Contact>> getAllContacts() {
        return checkContactInitialization().andThen(contactDao.getAll());
    }

    @NonNull
    public Single<Contact> getContactById(long contactId) {
        return checkContactInitialization().andThen(contactDao.getContactById(contactId));
    }

    private Completable checkContactInitialization() {
        return appFlagDao.getFlagByKey(AppFlagDB.AppFlags.INITIALIZED_CONTACTS.name())
                .onErrorReturn(throwable -> {
                    AppFlag appFlag = new AppFlag();
                    appFlag.setFlagKey(AppFlagDB.AppFlags.INITIALIZED_CONTACTS.name());
                    // We expect an empty result on first-open. Any other case, let's assume we've
                    // set it.
                    appFlag.setValue(!(throwable instanceof EmptyResultSetException));
                    return appFlag;
                })
                .flatMapCompletable(appFlag -> {
                    if (!appFlag.getValue()) {
                        // We haven't initialized the contact db yet. Insert our donation address.
                        appFlag.setValue(true);
                        return Completable.fromAction(() -> appFlagDao.insert(appFlag))
                                .andThen(addOrUpdate(DONATION_CONTACT));
                    } else {
                        return Completable.complete();
                    }
                })
                .onErrorResumeNext(throwable -> {
                    Timber.e(throwable, "Failed to initialize donation address");
                    // Don't stop user if we fail to set the donation contact.
                    return Completable.complete();
                });
    }

    @NonNull
    public Completable addOrUpdate(@NonNull Contact contact) {
        return Completable.fromAction(() -> contactDao.insert(contact));
    }

    @NonNull
    public Completable deleteContact(@NonNull Contact contact) {
        return Completable.fromAction(() -> contactDao.delete(contact));
    }
}
