package com.ximcoin.ximwallet.model.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.ximcoin.ximwallet.model.persistence.contacts.Contact;
import com.ximcoin.ximwallet.model.persistence.contacts.ContactDao;

@Database(entities = Contact.class, version = ContactDB.VERSION)
public abstract class ContactDB extends RoomDatabase {
    public static final String DATABASE_NAME = "ContactDB";
    public static final int VERSION = 1;

    public abstract ContactDao contactDao();
}
