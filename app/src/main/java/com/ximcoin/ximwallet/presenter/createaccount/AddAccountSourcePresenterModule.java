package com.ximcoin.ximwallet.presenter.createaccount;

import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.repository.FeesRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class AddAccountSourcePresenterModule {
    @Provides
    AddAccountSourceContract.AddAccountSourcePresenter provideAddAccountSourcePresenter(
            AddAccountSourceContract.AddAccountSourceView view,
            FeesRepository feesRepository,
            SchedulerProvider schedulerProvider) {
        return new AddAccountSourcePresenter(view, feesRepository, schedulerProvider);
    }
}
