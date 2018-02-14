package tech.duchess.luminawallet.presenter.nav;

import android.os.Bundle;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import tech.duchess.luminawallet.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.api.CoinMarketCapApi;
import tech.duchess.luminawallet.view.util.TextUtils;
import tech.duchess.luminawallet.view.util.ViewUtils;
import timber.log.Timber;

public class NavHeaderPresenter implements NavHeaderContract.NavHeaderPresenter {
    //TODO: Get from settings storage
    private static final String CURRENCY = "USD";
    private static final String CONVERSION_KEY = "NavHeaderPresenter.CONVERSION_KEY";
    private static final String CURRENCY_KEY = "NavHeaderPresenter.CURRENCY_KEY";
    private static final String LAST_UPDATED_KEY = "NavHeaderPresenter.LAST_UPDATED_KEY";

    @NonNull
    private final NavHeaderContract.NavHeaderView view;

    @NonNull
    private final CoinMarketCapApi api;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final CompositeDisposable disposables = new CompositeDisposable();

    private String conversion = null;
    private String currency = null;
    private long lastUpdatedSeconds = 0L;

    @Inject
    public NavHeaderPresenter(@NonNull NavHeaderContract.NavHeaderView view,
                              @NonNull CoinMarketCapApi api,
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
            view.showConversion(conversion, currency, lastUpdatedSeconds);
        }
    }

    @Override
    public void onUserRefreshConversion() {
        api.getConversionRate(CURRENCY)
                .compose(schedulerProvider.singleScheduler())
                .doOnSubscribe(disposable -> {
                    disposables.add(disposable);
                    view.showLoading(true);
                })
                .doAfterTerminate(() -> view.showLoading(false))
                .subscribe(conversionRate -> {
                    String conversion = conversionRate.getConvertedPrice();
                    String lastUpdated = conversionRate.getLastUpdated();
                    if (!TextUtils.isEmpty(conversion) && !TextUtils.isEmpty(lastUpdated)) {
                        this.conversion = conversion;
                        this.currency = CURRENCY;
                        this.lastUpdatedSeconds = Long.parseLong(lastUpdated);
                        view.showConversion(this.conversion, currency, this.lastUpdatedSeconds);
                    }
                }, throwable -> {
                    Timber.e(throwable, "Failed to get conversion rate for %s", CURRENCY);
                    view.showLoadError();
                });
    }

    @Override
    public void onUserRequestInfo() {
        view.showInfo();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(CONVERSION_KEY, conversion);
        outState.putString(CURRENCY_KEY, currency);
        outState.putLong(LAST_UPDATED_KEY, lastUpdatedSeconds);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        ViewUtils.whenNonNull(savedInstanceState, bundle -> {
            conversion = bundle.getString(CONVERSION_KEY);
            currency = bundle.getString(CURRENCY_KEY);
            lastUpdatedSeconds = bundle.getLong(LAST_UPDATED_KEY);
        });
    }

    @Override
    public void onViewDetached() {
        disposables.clear();
    }
}
