package ru.lextop.miningpoolhub.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides

import javax.inject.Named
import javax.inject.Singleton

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class AppModule {
    @Named(CONTEXT_APPLICATION)
    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

    @Named(SHARED_PREFERENCES_DEFAULT)
    @Provides
    @Singleton
    fun provideDefaultSharedPreferences(
        @Named(CONTEXT_APPLICATION) context: Context
    ): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Named(SHARED_PREFERENCES_PRIVATE)
    @Provides
    @Singleton
    fun providePrivateSharedPreferences(
        @Named(CONTEXT_APPLICATION) context: Context
    ): SharedPreferences {
        val privatePrefsName = context.packageName + "_preferences_private"
        return context.getSharedPreferences(privatePrefsName, Context.MODE_PRIVATE)
    }

    @Module
    companion object {
        const val CONTEXT_APPLICATION = "application"
        const val SHARED_PREFERENCES_DEFAULT = "default"
        const val SHARED_PREFERENCES_PRIVATE = "private"
    }
}
