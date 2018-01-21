package tech.duchess.luminawallet.presenter.account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.repository.AccountRepository;
import tech.duchess.luminawallet.presenter.common.BasePresenter;
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
                .subscribe(accounts -> {
                    view.showLoading(false);
                    if (accounts.isEmpty()) {
                        view.showNoAccountFound();
                    } else {
                        Account account = accounts.get(0);
                        if (!account.isOnNetwork()) {
                            view.showAccountNotOnNetwork(account);
                        } else {
                            view.showAccount(account);
                        }
                    }
                }, throwable -> {
                    Timber.e(throwable, "Failed to load accounts");
                    view.showLoading(false);
                    view.showAccountLoadFailure();
                });
    }

    @Override
    public void onAccountCreationReturned(boolean didCreateNewAccount) {
        refreshAccounts();
    }

    @Override
    public void onUserRequestAccountCreation(boolean isImportingSeed) {
        view.startCreateAccountFlow(isImportingSeed);
    }
}
