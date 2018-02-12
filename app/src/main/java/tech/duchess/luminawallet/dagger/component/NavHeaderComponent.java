package tech.duchess.luminawallet.dagger.component;

import dagger.Subcomponent;
import tech.duchess.luminawallet.presenter.nav.NavHeaderPresenterModule;
import tech.duchess.luminawallet.view.nav.NavHeaderView;
import tech.duchess.luminawallet.view.nav.NavHeaderViewModule;

@Subcomponent(modules = {NavHeaderViewModule.class, NavHeaderPresenterModule.class})
public interface NavHeaderComponent {
    void inject(NavHeaderView navHeaderView);
}
