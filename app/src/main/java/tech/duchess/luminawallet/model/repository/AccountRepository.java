package tech.duchess.luminawallet.model.repository;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import tech.duchess.luminawallet.model.api.HorizonApi;
import tech.duchess.luminawallet.model.persistence.HorizonDB;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.persistence.account.AccountDao;

public class AccountRepository {
    @NonNull
    private final HorizonApi horizonApi;

    @NonNull
    private final AccountDao accountDao;

    @Inject
    public AccountRepository(@NonNull HorizonApi horizonApi,
                             @NonNull HorizonDB horizonDB) {
        this.horizonApi = horizonApi;
        this.accountDao = horizonDB.accountDao();
    }

    public Single<List<Account>> getAccounts() {
        return accountDao.getAll();
    }

    public Single<List<Account>> testInsert() {
        return horizonApi.getAccount("GCEZWKCA5VLDNRLN3RPRJMRZOX3Z6G5CHCGSNFHEYVXM3XOJMDS674JZ")
                .flatMap(account -> {
                    accountDao.insertAll(account);
                    return getAccounts();
                });
    }
}
