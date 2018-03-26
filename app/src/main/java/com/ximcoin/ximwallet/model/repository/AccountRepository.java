package com.ximcoin.ximwallet.model.repository;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.HttpException;
import com.ximcoin.ximwallet.model.api.HorizonApi;
import com.ximcoin.ximwallet.model.persistence.HorizonDB;
import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.model.persistence.account.AccountDao;
import com.ximcoin.ximwallet.model.persistence.account.AccountPrivateKey;
import com.ximcoin.ximwallet.model.persistence.account.AccountPrivateKeyDao;
import com.ximcoin.ximwallet.model.persistence.account.DisconnectedAccount;

@Singleton
public class AccountRepository {
    @NonNull
    private final HorizonApi horizonApi;

    @NonNull
    private final AccountDao accountDao;

    @NonNull
    private final AccountPrivateKeyDao accountPrivateKeyDao;

    @NonNull
    private final Map<String, Account> inMemoryAccountCache = new LinkedHashMap<>();

    @Inject
    public AccountRepository(@NonNull HorizonApi horizonApi,
                             @NonNull HorizonDB horizonDB) {
        this.horizonApi = horizonApi;
        this.accountDao = horizonDB.accountDao();
        this.accountPrivateKeyDao = horizonDB.accountPrivateKeyDao();
    }

    public Single<List<Account>> getAllAccounts(boolean forceRefresh) {
        if (forceRefresh) {
            return getAllAccountsRemote();
        } else {
            return getAllAccountsCached();
        }
    }

    public Completable removeAccount(@NonNull String accountId) {
        return Completable.fromAction(() -> {
           accountDao.deleteAccountById(accountId);
           accountPrivateKeyDao.deleteByAccountId(accountId);
           inMemoryAccountCache.remove(accountId);
        });
    }

    public boolean isCached(@NonNull String accountId) {
        return inMemoryAccountCache.containsKey(accountId);
    }

    private Single<List<Account>> getAllAccountsCached() {
        return Single.just(new ArrayList<>(inMemoryAccountCache.values()));
    }

    private Single<List<Account>> getAllAccountsRemote() {
        return getAllAccountIds()
                .toObservable()
                .flatMapIterable(accountIds -> accountIds)
                .flatMap(accountId -> getAccountByIdRemote(accountId).toObservable())
                .toList();
    }

    private Single<List<String>> getAllAccountIds() {
        return accountPrivateKeyDao.getAllAccountIds();
    }

    public Single<AccountPrivateKey> getEncryptedSeed(@NonNull String accountId) {
        return accountPrivateKeyDao.getPrivateKeyForAccount(accountId);
    }

    public Single<Account> getAccountById(@NonNull String accountId, boolean forceRefresh) {
        if (forceRefresh) {
            return getAccountByIdRemote(accountId);
        } else {
            return getAccountByIdCache(accountId);
        }
    }

    private Single<Account> getAccountByIdCache(@NonNull String accountId) {
        return Single.just(inMemoryAccountCache.get(accountId));
    }

    private Single<Account> getAccountByIdRemote(@NonNull String accountId) {
        return horizonApi.getAccount(accountId)
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof HttpException
                            && ((HttpException) throwable).code() == 404) {
                        return Single.just(new DisconnectedAccount(accountId));
                    }

                    return Single.error(throwable);
                })
                .map(account -> {
                    accountDao.insert(account);
                    inMemoryAccountCache.put(accountId, account);
                    return account;
                });
    }
}
