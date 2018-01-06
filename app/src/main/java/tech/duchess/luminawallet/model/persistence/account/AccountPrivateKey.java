package tech.duchess.luminawallet.model.persistence.account;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class AccountPrivateKey {
    @NonNull
    @PrimaryKey
    private String accountId;

    @Embedded
    private EncryptedSeedPackage encryptedSeedPackage;

    public AccountPrivateKey() {
        this.accountId = Account.INVALID_ID;
    }

    public void setAccountId(@NonNull String accountId) {
        this.accountId = accountId;
    }

    @NonNull
    public String getAccountId() {
        return accountId;
    }

    public void setEncryptedSeedPackage(@NonNull EncryptedSeedPackage encryptedSeedPackage) {
        this.encryptedSeedPackage = encryptedSeedPackage;
    }

    public EncryptedSeedPackage getEncryptedSeedPackage() {
        return encryptedSeedPackage;
    }
}
