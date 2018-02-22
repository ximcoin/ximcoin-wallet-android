package tech.duchess.luminawallet.presenter.contacts;

import dagger.Module;
import dagger.Provides;
import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.repository.ContactRepository;

@Module
public class ViewContactPresenterModule {
    @Provides
    ViewContactContract.ViewContactPresenter provideViewContactPresenter(ViewContactContract.ViewContactView view,
                                                                         ContactRepository contactRepository,
                                                                         SchedulerProvider schedulerProvider) {
        return new ViewContactPresenter(view, contactRepository, schedulerProvider);
    }
}
