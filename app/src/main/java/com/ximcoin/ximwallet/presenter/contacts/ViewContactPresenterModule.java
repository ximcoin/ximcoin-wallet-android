package com.ximcoin.ximwallet.presenter.contacts;

import dagger.Module;
import dagger.Provides;
import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.repository.ContactRepository;

@Module
public class ViewContactPresenterModule {
    @Provides
    ViewContactContract.ViewContactPresenter provideViewContactPresenter(ViewContactContract.ViewContactView view,
                                                                         ContactRepository contactRepository,
                                                                         SchedulerProvider schedulerProvider) {
        return new ViewContactPresenter(view, contactRepository, schedulerProvider);
    }
}
