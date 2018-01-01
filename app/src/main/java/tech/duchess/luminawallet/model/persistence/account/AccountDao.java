package tech.duchess.luminawallet.model.persistence.account;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface AccountDao {
    @Query("SELECT * FROM account")
    Single<List<Account>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Account... accounts);
}
