package tech.duchess.luminawallet.model.dagger.module;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.squareup.moshi.Moshi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import tech.duchess.luminawallet.model.persistence.HorizonDB;

@Module
public class AppModule {
    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    public Moshi provideMoshi() {
        return new Moshi.Builder().build();
    }

    @Provides
    @Singleton
    public HorizonDB provideHorizonDB() {
        return Room.databaseBuilder(application, HorizonDB.class, HorizonDB.DATABASE_NAME).build();
    }
}
