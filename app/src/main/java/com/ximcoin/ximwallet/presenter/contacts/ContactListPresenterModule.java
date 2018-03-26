package com.ximcoin.ximwallet.presenter.contacts;

import dagger.Module;
import dagger.Provides;
import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.repository.ContactRepository;

@Module
public class ContactListPresenterModule {
    @Provides
    ContactListContract.ContactListPresenter provideContactListPresenter(ContactListContract.ContactListView view,
                                                                         ContactRepository contactRepository,
                                                                         SchedulerProvider schedulerProvider) {
        return new ContactListPresenter(view, contactRepository, schedulerProvider);
    }
}
