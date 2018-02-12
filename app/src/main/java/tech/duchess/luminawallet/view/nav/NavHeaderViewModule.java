package tech.duchess.luminawallet.view.nav;

import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import tech.duchess.luminawallet.presenter.nav.NavHeaderContract;

@Module
public class NavHeaderViewModule {
    @NonNull
    private final NavHeaderView navHeaderView;

    public NavHeaderViewModule(@NonNull NavHeaderView navHeaderView) {
        this.navHeaderView = navHeaderView;
    }

    @Provides
    NavHeaderContract.NavHeaderView provideNavHeaderView() {
        return navHeaderView;
    }
}
