package tech.duchess.luminawallet.presenter.contacts;

import dagger.Module;
import dagger.Provides;
import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.repository.ContactRepository;

@Module
public class EditContactPresenterModule {
    @Provides
    EditContactContract.EditContactPresenter provideEditContactPresenter(EditContactContract.EditContactView view,
                                                                         ContactRepository contactRepository,
                                                                         SchedulerProvider schedulerProvider) {
        return new EditContactPresenter(view, contactRepository, schedulerProvider);
    }
}
