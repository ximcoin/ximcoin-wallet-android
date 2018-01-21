package tech.duchess.luminawallet.presenter.account;

import dagger.Module;
import dagger.Provides;
import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.repository.AccountRepository;

@Module
public class AccountsPresenterModule {
    @Provides
    AccountsContract.AccountsPresenter provideAccountsPresenter(AccountsContract.AccountsView view,
                                                                AccountRepository accountRepository,
                                                                SchedulerProvider schedulerProvider) {
        return new AccountsPresenter(view, accountRepository, schedulerProvider);
    }
}
