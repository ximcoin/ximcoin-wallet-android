package com.ximcoin.ximwallet.presenter.nav;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.api.StellarTermTickerApi;
import com.ximcoin.ximwallet.view.util.TextUtils;
import com.ximcoin.ximwallet.view.util.ViewUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class NavHeaderPresenter implements NavHeaderContract.NavHeaderPresenter {
    private static final String CONVERSION_KEY = "NavHeaderPresenter.CONVERSION_KEY";
    private static final String LAST_UPDATED_KEY = "NavHeaderPresenter.LAST_UPDATED_KEY";

    @NonNull
    private final NavHeaderContract.NavHeaderView view;

    @NonNull
    private final StellarTermTickerApi api;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final CompositeDisposable disposables = new CompositeDisposable();

    private String conversion = null;
    private long lastUpdatedSeconds = 0L;

    @Inject
    public NavHeaderPresenter(@NonNull NavHeaderContract.NavHeaderView view,
                              @NonNull StellarTermTickerApi api,
                              @NonNull SchedulerProvider schedulerProvider) {
        this.view = view;
        this.api = api;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void onViewAttached() {
        if (TextUtils.isEmpty(conversion)) {
            view.showConversionUnknown();
            onUserRefreshConversion();
        } else {
            view.showConversion(conversion, "USD", lastUpdatedSeconds);
        }
    }

    @Override
    public void onUserRefreshConversion() {
        api.getConversionRate()
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    disposables.add(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> view.showLoading(false))
                .subscribe(conversionRate -> {
                    String conversion = conversionRate.getPriceUSD();
                    long lastUpdated = conversionRate.getLastUpdated();
                    if (!TextUtils.isEmpty(conversion) && lastUpdated > 0) {
                        this.conversion = conversion;
                        this.lastUpdatedSeconds = lastUpdated;
                        view.showConversion(this.conversion, "USD", this.lastUpdatedSeconds);
                    }
                }, throwable -> {
                    Timber.e(throwable, "Failed to get conversion rate");
                    view.showLoadError();
                });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(CONVERSION_KEY, conversion);
        outState.putLong(LAST_UPDATED_KEY, lastUpdatedSeconds);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        ViewUtils.whenNonNull(savedInstanceState, bundle -> {
            conversion = bundle.getString(CONVERSION_KEY);
            lastUpdatedSeconds = bundle.getLong(LAST_UPDATED_KEY);
        });
    }

    @Override
    public void onViewDetached() {
        disposables.clear();
    }
}
