package tech.duchess.luminawallet.presenter.account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private static final String CURRENT_ACCOUNT_KEY = "AccountsPresenter.CURRENT_ACCOUNT_KEY";
    private static final String DIRTIED_ACCOUNTS_KEY = "AccountsPresenter.DIRTIED_ACCOUNTS_KEY";

    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    private boolean hasLoadedAccounts = false;

    @Nullable
    private String currentAccountId;

    @NonNull
    private final Set<String> dirtiedAccounts = new HashSet<>();

    AccountsPresenter(@NonNull AccountsContract.AccountsView view,
                      @NonNull AccountRepository accountRepository,
                      @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.accountRepository = accountRepository;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void saveState(@Nullable Bundle bundle) {
        super.saveState(bundle);
        ViewUtils.whenNonNull(bundle, b -> {
            b.putBoolean(HAS_LOADED_ACCOUNTS_KEY, hasLoadedAccounts);
            b.putString(CURRENT_ACCOUNT_KEY, currentAccountId);
            b.putStringArrayList(DIRTIED_ACCOUNTS_KEY, new ArrayList<>(dirtiedAccounts));
        });
    }

    @Override
    public void start(@Nullable Bundle bundle) {
        ViewUtils.whenNonNull(bundle, b -> {
            hasLoadedAccounts = b.getBoolean(HAS_LOADED_ACCOUNTS_KEY, false);
            currentAccountId = b.getString(CURRENT_ACCOUNT_KEY, currentAccountId);
            List<String> dirtiedAccountList = b.getStringArrayList(DIRTIED_ACCOUNTS_KEY);
            if (dirtiedAccountList != null) {
                dirtiedAccounts.addAll(dirtiedAccountList);
            }
        });
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
                .doAfterTerminate(() -> {
                    view.showLoading(false);
                    dirtiedAccounts.clear();
                })
                .subscribe(accounts -> {
                    hasLoadedAccounts = true;
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
    public void onTransactionPosted(@NonNull Account account, @NonNull String destination) {
        if (currentAccountId == null) {
            return;
        }

        if (accountRepository.isCached(destination)) {
            dirtiedAccounts.add(destination);
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

        loadAccount(accountId, dirtiedAccounts.contains(accountId));
    }

    @Override
    public void onUserRequestRefresh() {
        ViewUtils.whenNonNull(currentAccountId, accountId -> loadAccount(accountId, true));
    }

    @Override
    public void onUserRequestRemoveAccount() {
        if (currentAccountId != null) {
            view.displayRemoveAccountConfirmation();
        }
    }

    @Override
    public void onUserConfirmRemoveAccount() {
        if (currentAccountId == null) {
            return;
        }

        accountRepository.removeAccount(currentAccountId)
                .compose(schedulerProvider.completableScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> {
                    view.showLoading(false);
                    refreshAccounts();
                })
                .subscribe(
                        () -> {
                        },
                        throwable -> {
                            Timber.e(throwable, "Failed to remove account");
                            view.showRemoveAccountFailure();
                        });
    }

    private void loadAccount(@NonNull String accountId, boolean forceRefresh) {
        accountRepository.getAccountById(accountId, forceRefresh)
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    addDisposable(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> view.showLoading(false))
                .subscribe(account -> {
                            if (dirtiedAccounts.contains(account.getAccount_id())) {
                                dirtiedAccounts.remove(account.getAccount_id());
                            }

                            updateViewForSelectedAccount(account);
                        },
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

    @Override
    public void onUserNavigatedToAbout() {
        view.navigateToAbout();
    }

    @Override
    public void onUserNavigatedToInflation() {
        if (currentAccountId != null) {
            view.navigateToInflation(currentAccountId);
        }
    }
}
