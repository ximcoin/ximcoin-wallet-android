package tech.duchess.luminawallet.presenter.account;

import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import tech.duchess.luminawallet.model.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.repository.AccountRepository;
import tech.duchess.luminawallet.model.util.SeedEncryptionUtil;
import timber.log.Timber;

public class AccountsPresenter {
    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final LifecycleProvider<ActivityEvent> lifecycleProvider;

    @Inject
    public AccountsPresenter(@NonNull AccountRepository accountRepository,
                             @NonNull SchedulerProvider schedulerProvider,
                             @NonNull LifecycleProvider<ActivityEvent> lifecycleProvider) {
        this.accountRepository = accountRepository;
        this.schedulerProvider = schedulerProvider;
        this.lifecycleProvider = lifecycleProvider;
    }

    public void test() {
        accountRepository.getAllAccountIds()
                .flatMap(accountIds -> {
                    return accountRepository.getEncryptedSeed(accountIds.get(0));
                })
                .flatMap(accountPrivateKey -> {
                    String decryptSeed = SeedEncryptionUtil.decryptSeed(accountPrivateKey.getEncryptedSeedPackage(), "password");
                    return accountRepository.getAccountById(accountPrivateKey.getAccountId(), false);
                })
                .compose(schedulerProvider.singleScheduler())
                .compose(lifecycleProvider.bindUntilEvent(ActivityEvent.DESTROY))
                .doOnError(Timber::e)
                .subscribe(account -> Timber.d(account.toString()));
    }
}
