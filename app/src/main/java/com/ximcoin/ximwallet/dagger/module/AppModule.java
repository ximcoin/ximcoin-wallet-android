package com.ximcoin.ximwallet.dagger.module;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.squareup.moshi.Moshi;

import org.stellar.sdk.Network;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.AndroidInjectionModule;
import dagger.android.ContributesAndroidInjector;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import com.ximcoin.ximwallet.EnvironmentConstants;
import com.ximcoin.ximwallet.dagger.scope.PerActivity;
import com.ximcoin.ximwallet.model.api.CoinMarketCapApi;
import com.ximcoin.ximwallet.model.api.CurlLoggingInterceptor;
import com.ximcoin.ximwallet.model.api.HorizonApi;
import com.ximcoin.ximwallet.model.persistence.AppFlagDB;
import com.ximcoin.ximwallet.model.persistence.ContactDB;
import com.ximcoin.ximwallet.model.persistence.HorizonDB;
import com.ximcoin.ximwallet.model.persistence.coinmarketcap.ConversionRateAdapter;
import com.ximcoin.ximwallet.view.about.AboutActivity;
import com.ximcoin.ximwallet.view.about.AboutActivityModule;
import com.ximcoin.ximwallet.view.account.AccountsActivity;
import com.ximcoin.ximwallet.view.account.AccountsActivityModule;
import com.ximcoin.ximwallet.view.contacts.ContactsActivity;
import com.ximcoin.ximwallet.view.contacts.ContactsActivityModule;
import com.ximcoin.ximwallet.view.createaccount.CreateAccountActivity;
import com.ximcoin.ximwallet.view.createaccount.CreateAccountActivityModule;
import com.ximcoin.ximwallet.view.inflation.InflationActivity;
import com.ximcoin.ximwallet.view.inflation.InflationActivityModule;
import com.ximcoin.ximwallet.view.exportidweb.ExportIdWebviewActivity;
import com.ximcoin.ximwallet.view.exportidweb.ExportIdWebviewActivityModule;

/**
 * Provides application-wide dependencies.
 */
@Module(includes = AndroidInjectionModule.class)
public abstract class AppModule {
    private static final String HORIZON_RETROFIT_QUALIFIER = "HorizonRetrofit";
    private static final String CMC_RETROFIT_QUALIFIER = "CMCRetrofit";

    @Provides
    @Singleton
    static HorizonDB provideHorizonDB(Application application) {
        return Room.databaseBuilder(application, HorizonDB.class, HorizonDB.DATABASE_NAME).build();
    }

    @Provides
    @Singleton
    static ContactDB provideContactDB(Application application) {
        return Room.databaseBuilder(application, ContactDB.class, ContactDB.DATABASE_NAME).build();
    }

    @Provides
    @Singleton
    static AppFlagDB provideAppFlagDB(Application application) {
        return Room.databaseBuilder(application, AppFlagDB.class, AppFlagDB.DATABASE_NAME).build();
    }

    @Provides
    @Singleton
    static Moshi provideMoshi() {
        return new Moshi.Builder()
                .add(new ConversionRateAdapter())
                .build();
    }

    @Provides
    @Singleton
    static Cache provideHttpCache(Application application) {
        return new Cache(application.getCacheDir(), 10 * 1024 * 1024);
    }

    @Provides
    @Singleton
    static OkHttpClient provideOkHttpClient(Cache cache) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        if (!EnvironmentConstants.IS_PRODUCTION) {
            CurlLoggingInterceptor curlLoggingInterceptor = new CurlLoggingInterceptor();
            curlLoggingInterceptor.setCurlOptions("-i");
            client.addNetworkInterceptor(curlLoggingInterceptor)
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("Accept", "application/json").build();
                        return chain.proceed(request);
                    });
        }
        client.cache(cache);
        return client.build();
    }

    @Provides
    @Singleton
    @Named(HORIZON_RETROFIT_QUALIFIER)
    static Retrofit provideHorizonRetrofit(Moshi moshi, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(EnvironmentConstants.HORIZON_API_ENDPOINT)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    @Named(CMC_RETROFIT_QUALIFIER)
    static Retrofit provideCMCRetrofit(Moshi moshi, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(EnvironmentConstants.COIN_MARKET_CAP_API_ENDPOINT)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    static HorizonApi providesHorizonApi(@Named(HORIZON_RETROFIT_QUALIFIER) Retrofit retrofit) {
        if (EnvironmentConstants.IS_PRODUCTION) {
            Network.usePublicNetwork();
        } else {
            Network.useTestNetwork();
        }
        return retrofit.create(HorizonApi.class);
    }

    @Provides
    @Singleton
    static CoinMarketCapApi providesCoinMarketCapApi(@Named(CMC_RETROFIT_QUALIFIER) Retrofit retrofit) {
        return retrofit.create(CoinMarketCapApi.class);
    }

    @PerActivity
    @ContributesAndroidInjector(modules = AccountsActivityModule.class)
    abstract AccountsActivity bindAccountsActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = CreateAccountActivityModule.class)
    abstract CreateAccountActivity bindCreateAccountActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = ContactsActivityModule.class)
    abstract ContactsActivity bindContactsActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = InflationActivityModule.class)
    abstract InflationActivity bindInflationActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = AboutActivityModule.class)
    abstract AboutActivity bindAboutActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = ExportIdWebviewActivityModule.class)
    abstract ExportIdWebviewActivity bindExportIdWebviewActivity();
}
