package ru.lextop.miningpoolhub.di

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.lextop.miningpoolhub.db.AppDatabase
import ru.lextop.miningpoolhub.db.BalanceDao
import ru.lextop.miningpoolhub.db.TickerDao
import javax.inject.Named
import javax.inject.Singleton

@Module
class DbModule {
    @Provides
    @Singleton
    fun provideDb(@Named(AppModule.CONTEXT_APPLICATION) context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app.db")
            .addCallback(AppDatabase.OnCreateCallback)
            .build()
    }

    @Provides
    @Singleton
    fun provideBalanceDao(db: AppDatabase): BalanceDao {
        return db.balanceDao()
    }

    @Provides
    @Singleton
    fun provideTickerDao(db: AppDatabase): TickerDao {
        return db.tickerDao()
    }
}
