package tech.duchess.luminawallet.model.persistence.account;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import io.reactivex.Single;

@Dao
public interface AccountPrivateKeyDao {
    @Query("SELECT * FROM accountprivatekey WHERE accountId = :accountId")
    Single<AccountPrivateKey> getPrivateKeyForAccount(@NonNull String accountId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(@NonNull AccountPrivateKey accountPrivateKey);
}
