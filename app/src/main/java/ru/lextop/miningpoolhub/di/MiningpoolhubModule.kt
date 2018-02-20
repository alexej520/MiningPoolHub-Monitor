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
import ru.lextop.miningpoolhub.api.MiningpoolhubApi
import ru.lextop.miningpoolhub.api.MiningpoolhubApiResponseDeserializer
import ru.lextop.miningpoolhub.preferences.PrivateAppPreferences
import ru.lextop.miningpoolhub.util.LiveDataCallAdapterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class MiningpoolhubModule {
    @Named(INTERCEPTOR_ROUTE)
    @Provides
    @Singleton
    fun provideRouteInterceptor(): Interceptor? {
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

    @Named(INTERCEPTOR_LOGGING)
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): Interceptor? {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Named(INTERCEPTOR_ADD_API_KEY)
    @Provides
    @Singleton
    fun provideApiKeyInterceptor(
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

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @Named(INTERCEPTOR_ROUTE) routeInterceptor: Interceptor?,
        @Named(INTERCEPTOR_ADD_API_KEY) accessTokenInterceptor: Interceptor,
        @Named(INTERCEPTOR_LOGGING) loggingInterceptor: Interceptor?
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        routeInterceptor?.let { builder.addInterceptor(it) }
        builder.addInterceptor(accessTokenInterceptor)
        loggingInterceptor?.let { builder.addInterceptor(it) }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(ApiResponse::class.java, MiningpoolhubApiResponseDeserializer)
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://miningpoolhub.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
    }

    companion object {
        const val INTERCEPTOR_ROUTE = "miningpoolhub_route"
        const val INTERCEPTOR_LOGGING = "miningpoolhub_logging"
        const val INTERCEPTOR_ADD_API_KEY = "miningpoolhub_addAccessToken"
    }
}
