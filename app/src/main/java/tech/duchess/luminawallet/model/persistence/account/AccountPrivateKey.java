package tech.duchess.luminawallet.model.persistence.account;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class AccountPrivateKey {
    private static final String INVALID_PRIVATE_KEY = "INVALID_PRIVATE_KEY";

    @NonNull
    @PrimaryKey
    private String accountId;

    @NonNull
    private String privateKey;

    public AccountPrivateKey() {
        this.accountId = Account.INVALID_ID;
        this.privateKey = INVALID_PRIVATE_KEY;
    }

    public void setAccountId(@NonNull String accountId) {
        this.accountId = accountId;
    }

    @NonNull
    public String getAccountId() {
        return accountId;
    }

    public void setPrivateKey(@NonNull String privateKey) {
        this.privateKey = privateKey;
    }

    @NonNull
    public String getPrivateKey() {
        return privateKey;
    }
}
