package ru.lextop.miningpoolhub.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.lextop.miningpoolhub.api.*
import ru.lextop.miningpoolhub.preferences.PrivateAppPreferences
import ru.lextop.miningpoolhub.util.LiveDataCallAdapterFactory
import javax.inject.Singleton

@Module
class WebServiceModule {


    // CoinmarketcapApi


    @Coinmarketcap(INTERCEPTOR_LOGGING)
    @Provides
    @Singleton
    fun provideCoinmarketcapLoggingInterceptor(): Interceptor? {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Coinmarketcap
    @Provides
    @Singleton
    fun provideCoinmarketcapOkHttpClient(
        @Coinmarketcap(INTERCEPTOR_LOGGING) loggingInterceptor: Interceptor?
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        loggingInterceptor?.let { builder.addInterceptor(it) }
        return builder.build()
    }

    @Coinmarketcap
    @Provides
    @Singleton
    fun provideCoinmarketcapGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(ApiResponse::class.java, CoinmarketcapApiResponseDeserializer)
            .create()
    }

    @Coinmarketcap
    @Provides
    @Singleton
    fun provideCoinmarketcapRetrofit(
        @Coinmarketcap client: OkHttpClient,
        @Coinmarketcap gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://api.coinmarketcap.com/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideCoinmarketcapApi(
        @Coinmarketcap retrofit: Retrofit
    ): CoinmarketcapApi {
        return retrofit.create(CoinmarketcapApi::class.java)
    }


    // MiningpoolhubApi


    @Miningpoolhub(INTERCEPTOR_ROUTE)
    @Provides
    @Singleton
    fun provideMiningpoolhubRouteInterceptor(): Interceptor? {
        return object : Interceptor {
            private val pattern =
                """^https://(.+?)${MiningpoolhubApi.QUERY_POOL}=([^&]*)&?(.*)""".toPattern()

            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                val url = request.url().toString()
                val matcher = pattern.matcher(url)
                val newUrl = if (matcher.matches()) {
                    val coinName = matcher.group(2)
                    val preCoin = matcher.group(1)
                    val postCoin = matcher.group(3)
                    "https://$coinName.$preCoin$postCoin"
                } else {
                    url
                }
                val newRequest = request.newBuilder()
                    .url(newUrl)
                    .build()
                return chain.proceed(newRequest)
            }
        }
    }

    @Miningpoolhub(INTERCEPTOR_LOGGING)
    @Provides
    @Singleton
    fun provideMiningpoolhubLoggingInterceptor(): Interceptor? {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Miningpoolhub(INTERCEPTOR_ADD_API_KEY)
    @Provides
    @Singleton
    fun provideMiningpoolhubApiKeyInterceptor(
        privateAppPreferences: PrivateAppPreferences
    ): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val apiKey = privateAppPreferences.miningpoolhubApiKey.get()
            val requestBuilder = request.newBuilder()
            if (apiKey.isNotEmpty()) {
                val newUrl = request.url().newBuilder()
                    .addQueryParameter(
                        MiningpoolhubApi.QUERY_API_KEY,
                        apiKey
                    ).build()
                requestBuilder.url(newUrl)
            }
            chain.proceed(requestBuilder.build())
        }
    }

    @Miningpoolhub
    @Provides
    @Singleton
    fun provideMiningpoolhubOkHttpClient(
        @Miningpoolhub(INTERCEPTOR_ROUTE) routeInterceptor: Interceptor?,
        @Miningpoolhub(INTERCEPTOR_ADD_API_KEY) accessTokenInterceptor: Interceptor,
        @Miningpoolhub(INTERCEPTOR_LOGGING) loggingInterceptor: Interceptor?
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        routeInterceptor?.let { builder.addInterceptor(it) }
        builder.addInterceptor(accessTokenInterceptor)
        loggingInterceptor?.let { builder.addInterceptor(it) }
        return builder.build()
    }

    @Miningpoolhub
    @Provides
    @Singleton
    fun provideMiningpoolhubGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(ApiResponse::class.java, MiningpoolhubApiResponseDeserializer)
            .create()
    }

    @Miningpoolhub
    @Provides
    @Singleton
    fun provideMiningpoolhubRetrofit(
        @Miningpoolhub client: OkHttpClient,
        @Miningpoolhub gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://miningpoolhub.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideMiningpoolhubApi(
        @Miningpoolhub retrofit: Retrofit
    ): MiningpoolhubApi {
        return retrofit.create(MiningpoolhubApi::class.java)
    }

    companion object {
        const val INTERCEPTOR_ROUTE = "route"
        const val INTERCEPTOR_LOGGING = "logging"
        const val INTERCEPTOR_ADD_API_KEY = "addAccessToken"
    }
}
