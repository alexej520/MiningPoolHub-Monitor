package ru.lextop.miningpoolhub.api

import android.arch.lifecycle.LiveData
import retrofit2.http.GET
import retrofit2.http.Query
import ru.lextop.miningpoolhub.vo.Balance

interface MiningpoolhubApi {
    @GET(PREFIX + "getminingandprofitsstatistics")
    fun getMiningAndProfitsStatistics(

    )

    @GET(PREFIX + "getautoswitchingandprofitsstatistics")
    fun getAutoSwitchingAndProfitsStatistics(

    )

    @GET(PREFIX + "getuserallbalances")
    fun getUserAllBalances(
    ): LiveData<ApiResponse<List<Balance>>>

    @GET(PREFIX + "getblockcount")
    fun getBlockCount(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    @GET(PREFIX + "getblocksfound")
    fun getBlocksFound(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    @GET(PREFIX + "getblockstats")
    fun getBlockStats(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    @GET(PREFIX + "getcurrentworkers")
    fun getCurrentWorkers(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    @GET(PREFIX + "getdashboarddata")
    fun getDashboardData(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    @GET(PREFIX + "getdifficulty")
    fun getDifficulty(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    @GET(PREFIX + "getestimatedtime")
    fun getEstimatedTime(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    @GET(PREFIX + "gethourlyhashrates")
    fun getHourlyHashrates(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    // getnavbardata

    @GET(PREFIX + "getpoolhashrate")
    fun getPoolHashrate(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    @GET(PREFIX + "getpoolinfo")
    fun getPoolInfo(

    )

    // getpoolsharerate

    @GET(PREFIX + "getpoolstatus")
    fun getpPoolStatus(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    @GET(PREFIX + "gettimesincelastblock")
    fun getTimeSinceLastBlock(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    @GET(PREFIX + "gettopcontributors")
    fun getTopContributors(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    @GET(PREFIX + "getuserbalance")
    fun getUserBalance(
        @Query(QUERY_COIN_NAME)
        coinName: String
    ): LiveData<ApiResponse<Balance>>

    @GET(PREFIX + "getuserhashrate")
    fun getUserHashrate(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    @GET(PREFIX + "getusersharerate")
    fun getUserSharerate(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    @GET(PREFIX + "getuserstatus")
    fun getUserStatus(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    @GET(PREFIX + "getusertransactions")
    fun getUserTransactions(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    // getuserworkers

    @GET(PREFIX + "public")
    fun getPoolStatistics(
        @Query(QUERY_COIN_NAME)
        coinName: String
    )

    companion object {
        private const val PREFIX = "index.php?page=api&action="

        const val QUERY_API_KEY = "api_key"
        const val QUERY_COIN_NAME = "coin_name"
    }
}
