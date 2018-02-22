package tech.duchess.luminawallet.model.persistence.contacts;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contact ORDER BY name ASC")
    Single<List<Contact>> getAll();

    @Query("SELECT * FROM contact WHERE id = :contactId")
    Single<Contact> getContactById(long contactId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Contact contact);

    @Delete
    void delete(Contact contact);
}
