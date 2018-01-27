package tech.duchess.luminawallet.dagger.module;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.squareup.moshi.Moshi;

import org.stellar.sdk.Network;

import javax.inject.Singleton;

import dagger.Binds;
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
import tech.duchess.luminawallet.EnvironmentConstants;
import tech.duchess.luminawallet.LuminaWalletApp;
import tech.duchess.luminawallet.dagger.scope.PerActivity;
import tech.duchess.luminawallet.model.api.CurlLoggingInterceptor;
import tech.duchess.luminawallet.model.api.HorizonApi;
import tech.duchess.luminawallet.model.persistence.HorizonDB;
import tech.duchess.luminawallet.view.account.AccountsActivity;
import tech.duchess.luminawallet.view.account.AccountsActivityModule;
import tech.duchess.luminawallet.view.createaccount.CreateAccountActivity;
import tech.duchess.luminawallet.view.createaccount.CreateAccountActivityModule;

/**
 * Provides application-wide dependencies.
 */
@Module(includes = AndroidInjectionModule.class)
public abstract class AppModule {
    @Binds
    @Singleton
    abstract Application application(LuminaWalletApp app);

    @Provides
    @Singleton
    static HorizonDB provideHorizonDB(Application application) {
        return Room.databaseBuilder(application, HorizonDB.class, HorizonDB.DATABASE_NAME).build();
    }

    @Provides
    @Singleton
    static Moshi provideMoshi() {
        return new Moshi.Builder().build();
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
    static Retrofit provideRetrofit(Moshi moshi, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(EnvironmentConstants.HORIZON_API_ENDPOINT)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    static HorizonApi providesHorizonApi(Retrofit retrofit) {
        if (EnvironmentConstants.IS_PRODUCTION) {
            Network.usePublicNetwork();
        } else {
            Network.useTestNetwork();
        }
        return retrofit.create(HorizonApi.class);
    }

    @PerActivity
    @ContributesAndroidInjector(modules = AccountsActivityModule.class)
    abstract AccountsActivity bindAccountsActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = CreateAccountActivityModule.class)
    abstract CreateAccountActivity bindCreateAccountActivity();
}