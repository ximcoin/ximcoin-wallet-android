package tech.duchess.luminawallet.presenter.account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import tech.duchess.luminawallet.model.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.repository.AccountRepository;
import tech.duchess.luminawallet.view.account.IAccountsView;
import tech.duchess.luminawallet.view.util.ViewBindingUtils;
import timber.log.Timber;

public class AccountsPresenter {
    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final LifecycleProvider<ActivityEvent> lifecycleProvider;

    @Nullable
    private IAccountsView view;

    @Inject
    public AccountsPresenter(@NonNull AccountRepository accountRepository,
                             @NonNull SchedulerProvider schedulerProvider,
                             @NonNull LifecycleProvider<ActivityEvent> lifecycleProvider) {
        this.accountRepository = accountRepository;
        this.schedulerProvider = schedulerProvider;
        this.lifecycleProvider = lifecycleProvider;
    }

    public void attachView(@NonNull IAccountsView view, boolean isFirstStart) {
        this.view = view;
        loadAccounts(isFirstStart);
    }

    public void detachView() {
        view = null;
    }

    private void loadAccounts(boolean isFirstStart) {
        accountRepository.getAllAccounts(isFirstStart)
                .compose(schedulerProvider.singleScheduler())
                .compose(lifecycleProvider.bindUntilEvent(ActivityEvent.DESTROY))
                .doOnSubscribe(disposable ->
                        ViewBindingUtils.whenNonNull(view, v -> v.showLoading(true)))
                .subscribe(accounts -> {
                    if (view == null) {
                        return;
                    }

                    view.showLoading(false);
                    if (accounts.isEmpty()) {
                        view.showNoAccountFound();
                    } else {
                        Account account = accounts.get(0);
                        if (!account.isOnNetwork()) {
                            view.showAccountNotOnNetwork(account.getAccount_id());
                        } else {
                            view.showAccount(account);
                        }
                    }
                }, throwable -> {
                    Timber.e(throwable, "Failed to load accounts");
                    ViewBindingUtils.whenNonNull(view, v -> {
                        v.showLoading(false);
                        v.showAccountLoadFailure();
                    });
                });
    }

    private class AccountNotFoundException extends Exception {
    }
}
