package tech.duchess.luminawallet.presenter.contacts;

import dagger.Module;
import dagger.Provides;
import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.repository.ContactRepository;

@Module
public class ContactListPresenterModule {
    @Provides
    ContactListContract.ContactListPresenter provideContactListPresenter(ContactListContract.ContactListView view,
                                                                         ContactRepository contactRepository,
                                                                         SchedulerProvider schedulerProvider) {
        return new ContactListPresenter(view, contactRepository, schedulerProvider);
    }
}
