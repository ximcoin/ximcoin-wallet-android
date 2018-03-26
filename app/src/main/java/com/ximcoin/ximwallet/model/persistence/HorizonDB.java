package com.ximcoin.ximwallet.model.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.model.persistence.account.AccountDao;
import com.ximcoin.ximwallet.model.persistence.account.AccountPrivateKey;
import com.ximcoin.ximwallet.model.persistence.account.AccountPrivateKeyDao;

@Database(entities = {Account.class, AccountPrivateKey.class}, version = HorizonDB.VERSION)
public abstract class HorizonDB extends RoomDatabase {
    public static final String DATABASE_NAME = "HorizonDB";
    public static final int VERSION = 1;

    public abstract AccountDao accountDao();

    public abstract AccountPrivateKeyDao accountPrivateKeyDao();
}
