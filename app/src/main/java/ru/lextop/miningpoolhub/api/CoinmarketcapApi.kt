package ru.lextop.miningpoolhub.api

import android.arch.lifecycle.LiveData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.lextop.miningpoolhub.vo.Ticker

interface CoinmarketcapApi {
    @GET("ticker/")
    fun getTickers(
        @Query("start")
        start: Int? = null,
        @Query("limit")
        limit: Int? = null,
        @Query("convert")
        convert: String? = null
    ): ApiResponse<List<Ticker>>

    /** @return list with single element */
    @GET("ticker/{id}/")
    fun getTicker(
        @Path("id")
        id: String,
        @Query("convert")
        convert: String? = null
    ): LiveData<ApiResponse<List<Ticker>>>

    // TODO: implement response
    @GET("global/")
    fun getGlobal(
        @Query("convert")
        convert: String? = null
    ): ApiResponse<*>

    companion object {
        const val CONVERT_AUD = "AUD"
        const val CONVERT_BRL = "BRL"
        const val CONVERT_CAD = "CAD"
        const val CONVERT_CHF = "CHF"
        const val CONVERT_CLP = "CLP"
        const val CONVERT_CNY = "CNY"
        const val CONVERT_CZK = "CZK"
        const val CONVERT_DKK = "DKK"
        const val CONVERT_EUR = "EUR"
        const val CONVERT_GBP = "GBP"
        const val CONVERT_HKD = "HKD"
        const val CONVERT_HUF = "HUF"
        const val CONVERT_IDR = "IDR"
        const val CONVERT_ILS = "ILS"
        const val CONVERT_INR = "INR"
        const val CONVERT_JPY = "JPY"
        const val CONVERT_KRW = "KRW"
        const val CONVERT_MXN = "MXN"
        const val CONVERT_MYR = "MYR"
        const val CONVERT_NOK = "NOK"
        const val CONVERT_NZD = "NZD"
        const val CONVERT_PHP = "PHP"
        const val CONVERT_PKR = "PKR"
        const val CONVERT_PLN = "PLN"
        const val CONVERT_RUB = "RUB"
        const val CONVERT_SEK = "SEK"
        const val CONVERT_SGD = "SGD"
        const val CONVERT_THB = "THB"
        const val CONVERT_TRY = "TRY"
        const val CONVERT_TWD = "TWD"
        const val CONVERT_USD = "USD"
        const val CONVERT_ZAR = "ZAR"
    }
}
