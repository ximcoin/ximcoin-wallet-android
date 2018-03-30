package com.ximcoin.ximwallet.presenter.account;

import dagger.Module;
import dagger.Provides;
import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.repository.AccountRepository;
import com.ximcoin.ximwallet.model.repository.FeesRepository;

@Module
public class AccountsPresenterModule {
    @Provides
    AccountsContract.AccountsPresenter provideAccountsPresenter(AccountsContract.AccountsView view,
                                                                AccountRepository accountRepository,
                                                                FeesRepository feesRepository,
                                                                SchedulerProvider schedulerProvider) {
        return new AccountsPresenter(view, accountRepository, feesRepository, schedulerProvider);
    }
}
