package tech.duchess.luminawallet.model.repository;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import tech.duchess.luminawallet.model.persistence.ContactDB;
import tech.duchess.luminawallet.model.persistence.contacts.Contact;
import tech.duchess.luminawallet.model.persistence.contacts.ContactDao;

public class ContactRepository {
    @NonNull
    private final ContactDao contactDao;

    @Inject
    public ContactRepository(@NonNull ContactDB contactDB) {
        this.contactDao = contactDB.contactDao();
    }

    @NonNull
    public Single<List<Contact>> getAllContacts() {
        return contactDao.getAll();
    }

    @NonNull
    public Single<Contact> getContactById(long contactId) {
        return contactDao.getContactById(contactId);
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
