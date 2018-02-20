package ru.lextop.miningpoolhub.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.lextop.miningpoolhub.api.ApiResponse
import ru.lextop.miningpoolhub.api.CoinmarketcapApi
import ru.lextop.miningpoolhub.api.CoinmarketcapApiResponseDeserializer
import ru.lextop.miningpoolhub.util.LiveDataCallAdapterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class CoinmarketcapModule {

    @Named(INTERCEPTOR_LOGGING)
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): Interceptor? {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @Named(INTERCEPTOR_LOGGING) loggingInterceptor: Interceptor?
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        loggingInterceptor?.let { builder.addInterceptor(it) }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(ApiResponse::class.java, CoinmarketcapApiResponseDeserializer)
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://api.coinmarketcap.com/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideCoinmarketcapApi(retrofit: Retrofit) {

    }

    @Provides
    @Singleton
    fun provideMiningpoolhubApi(retrofit: Retrofit): CoinmarketcapApi {
        return retrofit.create(CoinmarketcapApi::class.java)
    }


    companion object {
        const val INTERCEPTOR_LOGGING = "coinmarketcap_logging"
    }
}
