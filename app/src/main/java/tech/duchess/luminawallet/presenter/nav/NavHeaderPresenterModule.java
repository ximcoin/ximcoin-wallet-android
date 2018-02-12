package tech.duchess.luminawallet.presenter.nav;

import dagger.Module;
import dagger.Provides;
import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.api.CoinMarketCapApi;

@Module
public class NavHeaderPresenterModule {
    @Provides
    NavHeaderContract.NavHeaderPresenter provideNavHeaderPresenter(NavHeaderContract.NavHeaderView view,
                                                                   CoinMarketCapApi api,
                                                                   SchedulerProvider schedulerProvider) {
        return new NavHeaderPresenter(view, api, schedulerProvider);
    }
}
