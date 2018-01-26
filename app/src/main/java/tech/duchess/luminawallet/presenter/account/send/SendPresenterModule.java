package tech.duchess.luminawallet.presenter.account.send;

import dagger.Module;
import dagger.Provides;

@Module
public class SendPresenterModule {
    @Provides
    SendContract.SendPresenter provideAccountsPresenter(SendContract.SendView view) {
        return new SendPresenter(view);
    }
}
