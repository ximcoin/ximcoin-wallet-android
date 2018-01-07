package tech.duchess.luminawallet.model.repository;

import android.arch.persistence.room.EmptyResultSetException;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import tech.duchess.luminawallet.model.api.HorizonApi;
import tech.duchess.luminawallet.model.persistence.HorizonDB;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.persistence.account.AccountDao;
import tech.duchess.luminawallet.model.persistence.account.AccountPrivateKey;
import tech.duchess.luminawallet.model.persistence.account.AccountPrivateKeyDao;
import tech.duchess.luminawallet.model.persistence.account.EncryptedSeedPackage;

public class AccountRepository {
    @NonNull
    private final HorizonApi horizonApi;

    @NonNull
    private final AccountDao accountDao;

    @NonNull
    private final AccountPrivateKeyDao accountPrivateKeyDao;

    @Inject
    public AccountRepository(@NonNull HorizonApi horizonApi,
                             @NonNull HorizonDB horizonDB) {
        this.horizonApi = horizonApi;
        this.accountDao = horizonDB.accountDao();
        this.accountPrivateKeyDao = horizonDB.accountPrivateKeyDao();
    }

    public Single<List<String>> getAllAccountIds() {
        // Note that we never have to do a network transaction to figure this out. All of our
        // managed accounts are stored on disk.
        return accountPrivateKeyDao.getAllAccountIds();
    }

    public Single<AccountPrivateKey> getEncryptedSeed(@NonNull String accountId) {
        return accountPrivateKeyDao.getPrivateKeyForAccount(accountId);
    }

    public Single<Account> getAccountById(@NonNull String accountId, boolean forceRefresh) {
        if (forceRefresh) {
            return getAccountByIdRemote(accountId);
        } else {
            return Single.amb(Arrays.asList(getAccountByIdCache(accountId),
                    getAccountByIdRemote(accountId)));
        }
    }

    private Single<Account> getAccountByIdCache(@NonNull String accountId) {
        return accountDao.getAccountById(accountId)
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof EmptyResultSetException) {
                        // Not an error. This may be our first pull.
                        return Single.never();
                    }

                    return Single.error(throwable);
                });
    }

    private Single<Account> getAccountByIdRemote(@NonNull String accountId) {
        return horizonApi.getAccount(accountId)
                .map(account -> {
                    // Cache on disk
                    accountDao.insertAll(account);
                    return account;
                });
    }
}
