package com.ximcoin.ximwallet.presenter.account.transactions;

import com.squareup.moshi.Moshi;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import com.ximcoin.ximwallet.model.api.HorizonApi;

@Module
public class TransactionsPresenterModule {
    @Provides
    TransactionsContract.TransactionsPresenter provideTransactionsPresenter(
            TransactionsContract.TransactionsView view,
            HorizonApi horizonApi,
            OkHttpClient okHttpClient,
            Moshi moshi) {
        return new TransactionsPresenter(view, horizonApi, okHttpClient, moshi);
    }
}
