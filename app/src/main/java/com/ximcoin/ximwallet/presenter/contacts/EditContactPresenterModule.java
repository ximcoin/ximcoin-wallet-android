package com.ximcoin.ximwallet.presenter.contacts;

import dagger.Module;
import dagger.Provides;
import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.repository.ContactRepository;

@Module
public class EditContactPresenterModule {
    @Provides
    EditContactContract.EditContactPresenter provideEditContactPresenter(EditContactContract.EditContactView view,
                                                                         ContactRepository contactRepository,
                                                                         SchedulerProvider schedulerProvider) {
        return new EditContactPresenter(view, contactRepository, schedulerProvider);
    }
}
