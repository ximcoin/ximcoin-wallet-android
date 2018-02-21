package tech.duchess.luminawallet.presenter.account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.repository.AccountRepository;
import tech.duchess.luminawallet.presenter.common.BasePresenter;
import tech.duchess.luminawallet.view.util.TextUtils;
import tech.duchess.luminawallet.view.util.ViewUtils;
import timber.log.Timber;

public class AccountsPresenter extends BasePresenter<AccountsContract.AccountsView>
        implements AccountsContract.AccountsPresenter {
    private static final String HAS_LOADED_ACCOUNTS_KEY =
            "AccountsPresenter.HAS_LOADED_ACCOUNTS_KEY";

    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    private boolean hasLoadedAccounts = false;

    @Nullable
    private String currentAccountId;

    AccountsPresenter(@NonNull AccountsContract.AccountsView view,
                      @NonNull AccountRepository accountRepository,
                      @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.accountRepository = accountRepository;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void start(@Nullable Bundle bundle) {
        ViewUtils.whenNonNull(bundle,
                b -> b.putBoolean(HAS_LOADED_ACCOUNTS_KEY, hasLoadedAccounts));
    }

    @Override
    public void resume() {
        super.resume();
        loadAccounts(!hasLoadedAccounts);
    }

    public void refreshAccounts() {
        loadAccounts(true);
    }

    private void loadAccounts(boolean deepPoll) {
        accountRepository.getAllAccounts(deepPoll)
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> view.showLoading(false))
                .subscribe(accounts -> {
                    view.updateAccountList(accounts);
                    updateViewForSelectedAccount(accounts.isEmpty() ? null
                            : getCurrentAccount(accounts, currentAccountId));
                }, throwable -> {
                    Timber.e(throwable, "Failed to load accounts");
                    view.updateAccountList(new ArrayList<>());
                    currentAccountId = null;
                    view.showAccountsLoadFailure();
                });
    }

    private Account getCurrentAccount(@NonNull List<Account> accounts,
                                      @Nullable String currentAccountId) {
        if (TextUtils.isEmpty(currentAccountId)) {
            return accounts.get(0);
        }

        return Observable.fromIterable(accounts)
                .filter(account -> currentAccountId.equals(account.getAccount_id()))
                .defaultIfEmpty(accounts.get(0))
                .blockingFirst();
    }

    private void updateViewForSelectedAccount(@Nullable Account account) {
        if (account == null) {
            currentAccountId = null;
            view.showNoAccountFound();
            return;
        }

        currentAccountId = account.getAccount_id();
        if (!account.isOnNetwork()) {
            view.showAccountNotOnNetwork(account);
        } else {
            view.showAccount(account);
        }
    }

    @Override
    public void onAccountCreationReturned(boolean didCreateNewAccount) {
        refreshAccounts();
    }

    @Override
    public void onUserRequestAccountCreation(boolean isImportingSeed) {
        view.startCreateAccountFlow(isImportingSeed);
    }

    @Override
    public void onTransactionPosted(@NonNull Account account) {
        if (currentAccountId == null) {
            return;
        }

        if (currentAccountId.equals(account.getAccount_id())) {
            view.updateForTransaction(account);
        }
    }

    @Override
    public void onAccountNavigated(@NonNull String accountId) {
        if (accountId.equals(currentAccountId)) {
            return;
        }

        accountRepository.getAccountById(accountId, false)
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> view.showLoading(false))
                .subscribe(this::updateViewForSelectedAccount,
                        throwable -> {
                            Timber.e(throwable, "Failed to navigate to account");
                            view.showAccountNavigationFailure();
                        });
    }

    @Override
    public void onUserNavigatedToAddAccount() {
        view.navigateToCreateAccountFlow(!TextUtils.isEmpty(currentAccountId));
    }

    @Override
    public void onUserNavigatedToContacts() {
        view.navigateToContacts();
    }
}
