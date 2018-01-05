package tech.duchess.luminawallet.presenter.account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

import javax.inject.Inject;

import tech.duchess.luminawallet.model.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.repository.AccountRepository;
import tech.duchess.luminawallet.view.util.ViewBindingUtils;
import tech.duchess.luminawallet.view.account.IAccountsView;
import timber.log.Timber;

public class AccountsPresenter {
    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final LifecycleProvider<ActivityEvent> lifecycleProvider;

    @Nullable
    private IAccountsView walletsView;

    @Inject
    public AccountsPresenter(@NonNull AccountRepository accountRepository,
                             @NonNull SchedulerProvider schedulerProvider,
                             @NonNull LifecycleProvider<ActivityEvent> lifecycleProvider) {
        this.accountRepository = accountRepository;
        this.schedulerProvider = schedulerProvider;
        this.lifecycleProvider = lifecycleProvider;
    }

    public void attachView(@NonNull IAccountsView walletsView) {
        this.walletsView = walletsView;
    }

    public void loadUI(boolean forceRefresh) {
        accountRepository.getAccounts()
                .compose(schedulerProvider.singleScheduler())
                .compose(lifecycleProvider.bindUntilEvent(ActivityEvent.DESTROY))
                .doAfterTerminate(() -> setLoading(false))
                .doOnSubscribe(disposable -> setLoading(true))
                .subscribe(this::updateWallets, this::showError);
    }

    private void setLoading(boolean isLoading) {
        ViewBindingUtils.whenNonNull(walletsView, view -> view.showLoading(isLoading));
    }

    public void onViewDetached() {
        this.walletsView = null;
    }

    private void updateWallets(@NonNull List<Account> accountList) {
        ViewBindingUtils.whenNonNull(walletsView, view -> {
            view.showLoading(false);
            view.showAccounts(accountList);
        });
    }

    private void showError(@NonNull Throwable throwable) {
        Timber.e(throwable, "Failed to load accounts");
        ViewBindingUtils.whenNonNull(walletsView, view -> {
            view.showLoadError();
            view.showLoading(false);
        });
    }
}
