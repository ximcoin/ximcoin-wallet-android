package com.ximcoin.ximwallet.presenter.nav;

import dagger.Module;
import dagger.Provides;
import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.api.CoinMarketCapApi;

@Module
public class NavHeaderPresenterModule {
    @Provides
    NavHeaderContract.NavHeaderPresenter provideNavHeaderPresenter(NavHeaderContract.NavHeaderView view,
                                                                   CoinMarketCapApi api,
                                                                   SchedulerProvider schedulerProvider) {
        return new NavHeaderPresenter(view, api, schedulerProvider);
    }
}
