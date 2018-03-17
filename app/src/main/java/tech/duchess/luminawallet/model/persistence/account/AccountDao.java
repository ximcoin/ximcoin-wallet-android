package tech.duchess.luminawallet.model.persistence.account;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface AccountDao {
    @Query("SELECT * FROM account")
    Single<List<Account>> getAll();

    @Query("SELECT * FROM account WHERE account_id = :accountId")
    Single<Account> getAccountById(@NonNull String accountId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Account... accounts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Account account);

    @Query("DELETE FROM account WHERE account_id = :accountId")
    void deleteAccountById(@NonNull String accountId);
}
