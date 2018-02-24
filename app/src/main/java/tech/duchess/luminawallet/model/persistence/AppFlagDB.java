package tech.duchess.luminawallet.model.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import tech.duchess.luminawallet.model.persistence.appflag.AppFlag;
import tech.duchess.luminawallet.model.persistence.appflag.AppFlagDao;

@Database(entities = AppFlag.class, version = AppFlagDB.VERSION)
public abstract class AppFlagDB extends RoomDatabase {
    public static final String DATABASE_NAME = "AppFlagDB";
    public static final int VERSION = 1;

    public enum AppFlags {
        INITIALIZED_CONTACTS
    }

    public abstract AppFlagDao appFlagDao();
}