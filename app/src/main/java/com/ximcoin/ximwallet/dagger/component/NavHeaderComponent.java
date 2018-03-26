package com.ximcoin.ximwallet.dagger.component;

import dagger.Subcomponent;
import com.ximcoin.ximwallet.presenter.nav.NavHeaderPresenterModule;
import com.ximcoin.ximwallet.view.nav.NavHeaderView;
import com.ximcoin.ximwallet.view.nav.NavHeaderViewModule;

@Subcomponent(modules = {NavHeaderViewModule.class, NavHeaderPresenterModule.class})
public interface NavHeaderComponent {
    void inject(NavHeaderView navHeaderView);
}
