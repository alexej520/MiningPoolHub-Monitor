package ru.lextop.miningpoolhub.api

import android.arch.lifecycle.LiveData
import retrofit2.http.GET
import retrofit2.http.Query
import ru.lextop.miningpoolhub.vo.AutoSwitchingStat
import ru.lextop.miningpoolhub.vo.Balance
import ru.lextop.miningpoolhub.vo.CoinMiningStat

interface MiningpoolhubApi {
    @GET(PREFIX + "getminingandprofitsstatistics")
    fun getMiningAndProfitsStatistics(
    ): LiveData<ApiResponse<List<CoinMiningStat>>>

    @GET(PREFIX + "getautoswitchingandprofitsstatistics")
    fun getAutoSwitchingAndProfitsStatistics(
    ): LiveData<ApiResponse<List<AutoSwitchingStat>>>

    @GET(PREFIX + "getuserallbalances")
    fun getUserAllBalances(
    ): LiveData<ApiResponse<List<Balance>>>

    @GET(PREFIX + "getblockcount")
    fun getBlockCount(
        @Query(QUERY_POOL)
        coinName: String
    )

    @GET(PREFIX + "getblocksfound")
    fun getBlocksFound(
        @Query(QUERY_POOL)
        coinName: String
    )

    @GET(PREFIX + "getblockstats")
    fun getBlockStats(
        @Query(QUERY_POOL)
        coinName: String
    )

    @GET(PREFIX + "getcurrentworkers")
    fun getCurrentWorkers(
        @Query(QUERY_POOL)
        coinName: String
    )

    @GET(PREFIX + "getdashboarddata")
    fun getDashboardData(
        @Query(QUERY_POOL)
        coinName: String
    )

    @GET(PREFIX + "getdifficulty")
    fun getDifficulty(
        @Query(QUERY_POOL)
        coinName: String
    )

    @GET(PREFIX + "getestimatedtime")
    fun getEstimatedTime(
        @Query(QUERY_POOL)
        coinName: String
    )

    @GET(PREFIX + "gethourlyhashrates")
    fun getHourlyHashrates(
        @Query(QUERY_POOL)
        coinName: String
    )

    // getnavbardata

    @GET(PREFIX + "getpoolhashrate")
    fun getPoolHashrate(
        @Query(QUERY_POOL)
        coinName: String
    )

    @GET(PREFIX + "getpoolinfo")
    fun getPoolInfo(
        @Query(QUERY_POOL)
        coinName: String
    )

    // getpoolsharerate

    @GET(PREFIX + "getpoolstatus")
    fun getpPoolStatus(
        @Query(QUERY_POOL)
        coinName: String
    )

    @GET(PREFIX + "gettimesincelastblock")
    fun getTimeSinceLastBlock(
        @Query(QUERY_POOL)
        coinName: String
    )

    @GET(PREFIX + "gettopcontributors")
    fun getTopContributors(
        @Query(QUERY_POOL)
        coinName: String
    )

    @GET(PREFIX + "getuserbalance")
    fun getUserBalance(
        @Query(QUERY_POOL)
        coinName: String
    ): LiveData<ApiResponse<Balance>>

    @GET(PREFIX + "getuserhashrate")
    fun getUserHashrate(
        @Query(QUERY_POOL)
        coinName: String
    )

    @GET(PREFIX + "getusersharerate")
    fun getUserSharerate(
        @Query(QUERY_POOL)
        coinName: String
    )

    @GET(PREFIX + "getuserstatus")
    fun getUserStatus(
        @Query(QUERY_POOL)
        coinName: String
    )

    @GET(PREFIX + "getusertransactions")
    fun getUserTransactions(
        @Query(QUERY_POOL)
        coinName: String
    )

    // getuserworkers

    @GET(PREFIX + "public")
    fun getPoolStatistics(
        @Query(QUERY_POOL)
        coinName: String
    )

    companion object {
        private const val PREFIX = "index.php?page=api&action="

        const val QUERY_API_KEY = "api_key"
        const val QUERY_POOL = "coin_name"

        const val POOL_ADZCOIN = "adzcoin"
        const val COIN_AURORACOIN_QUBIT = "auroracoin-qubit"
        const val COIN_BITCOIN = "bitcoin"
        const val COIN_BITCOIN_CASH = "bitcoin-cash"
        const val COIN_BITCOIN_GOLD = "bitcoin-gold"
        const val COIN_DASH = "dash"
        const val COIN_DIGIBYTE_GROESTL = "digibyte-groestl"
        const val COIN_DIGIBYTE_QUBIT = "digibyte-qubit"
        const val COIN_DIGIBYTE_SKEIN = "digibyte-skein"
        const val COIN_ELECTRONEUM = "electroneum"
        const val COIN_ETHEREUM = "ethereum"
        const val COIN_ETHEREUM_CLASSIC = "ethereum-classic"
        const val COIN_EXPANSE = "expanse"
        const val COIN_FEATHERCOIN = "feathercoin"
        const val COIN_GAMECREDITS = "gamecredits"
        const val COIN_GEOCOIN = "geocoin"
        const val COIN_GLOBALBOOSTY = "globalboosty"
        const val COIN_GROESTLCOIN = "groestlcoin"
        const val COIN_LITECOIN = "litecoin"
        const val COIN_MAXCOIN = "maxcoin"
        const val COIN_MONACOIN = "monacoin"
        const val COIN_MONERO = "monero"
        const val COIN_MUSICOIN = "musicoin"
        const val COIN_MYRIADCOIN_GROESTL = "myriadcoin-groestl"
        const val COIN_MYRIADCOIN_SKEIN = "myriadcoin-skein"
        const val COIN_MYRIADCOIN_YESCRYPT = "myriadcoin-yescrypt"
        const val COIN_SEXCOIN = "sexcoin"
        const val COIN_SIACOIN = "siacoin"
        const val COIN_STARTCOIN = "startcoin"
        const val COIN_VERGE_SCRYPT = "verge-scrypt"
        const val COIN_VERTCOIN = "vertcoin"
        const val COIN_ZCASH = "zcash"
        const val COIN_ZCLASSIC = "zclassic"
        const val COIN_ZCOIN = "zcoin"
        const val COIN_ZENCASH = "zencash"
    }
}
