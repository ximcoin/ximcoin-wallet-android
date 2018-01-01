package tech.duchess.luminawallet.model.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.persistence.account.AccountDao;

@Database(entities = {Account.class}, version = 1)
public abstract class HorizonDB extends RoomDatabase {
    public static final String DATABASE_NAME = "HorizonDB";
    public static final int VERSION = 1;

    public abstract AccountDao accountDao();
}
