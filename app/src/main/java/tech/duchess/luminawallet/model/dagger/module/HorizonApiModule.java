package tech.duchess.luminawallet.model.dagger.module;

import android.app.Application;

import com.squareup.moshi.Moshi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import tech.duchess.luminawallet.EnvironmentConstants;
import tech.duchess.luminawallet.model.api.CurlLoggingInterceptor;
import tech.duchess.luminawallet.model.api.HorizonApi;

@Module
public class HorizonApiModule {
    @Provides
    @Singleton
    Cache provideHttpCache(Application application) {
        return new Cache(application.getCacheDir(), 10 * 1024 * 1024);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache) {
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
    Retrofit provideRetrofit(Moshi moshi, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(EnvironmentConstants.HORIZON_API_ENDPOINT)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    public HorizonApi providesHorizonApi(Retrofit retrofit) {
        return retrofit.create(HorizonApi.class);
    }
}
