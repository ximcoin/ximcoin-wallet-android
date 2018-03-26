package com.ximcoin.ximwallet.model.persistence.account;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface AccountPrivateKeyDao {
    @Query("SELECT * FROM accountprivatekey WHERE accountId = :accountId")
    Single<AccountPrivateKey> getPrivateKeyForAccount(@NonNull String accountId);

    @Query("SELECT accountId FROM accountprivatekey")
    Single<List<String>> getAllAccountIds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(@NonNull AccountPrivateKey accountPrivateKey);

    @Query("DELETE FROM accountprivatekey WHERE accountId = :accountId")
    void deleteByAccountId(@NonNull String accountId);
}
