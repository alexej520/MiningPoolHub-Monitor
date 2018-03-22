package ru.lextop.miningpoolhub.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.api.ApiResponse
import ru.lextop.miningpoolhub.api.CoinmarketcapApi
import ru.lextop.miningpoolhub.api.MiningpoolhubApi
import ru.lextop.miningpoolhub.api.getTickers
import ru.lextop.miningpoolhub.db.AppDatabase
import ru.lextop.miningpoolhub.db.BalanceDao
import ru.lextop.miningpoolhub.vo.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalanceRepository @Inject constructor(
    private val db: AppDatabase,
    private val appExecutors: AppExecutors,
    private val balanceDao: BalanceDao,
    private val miningpoolhubApi: MiningpoolhubApi,
    private val coinmarketcapApi: CoinmarketcapApi
) {
    private fun loadBalancePairsWithOldTickers(convert: String): LiveData<Resource<List<BalancePair>>> =
        object : NetworkBoundResource<List<BalancePair>, List<Balance>>(appExecutors) {
            override fun saveCallResult(body: List<Balance>) {
                db.runInTransaction {
                    balanceDao.cleanBalances()
                    balanceDao.insertBalances(body)
                }
            }

            override fun shouldFetch(data: List<BalancePair>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<BalancePair>> {
                return object : LiveData<List<BalancePair>>() {
                    override fun onActive() {
                        appExecutors.diskIO.execute {
                            val balances = balanceDao.loadBalances()

                            val balancePairs = balances.map { current ->
                                val ticker = balanceDao.loadTicker(current.coin, convert)
                                if (ticker != null) {
                                    val converted =
                                        current * (ticker.convertedStats.price ?: Double.NaN)
                                    converted.currency = ticker.convertedCurrency
                                    BalancePair(current, converted)
                                } else {
                                    BalancePair(current, null)
                                }
                            }

                            postValue(balancePairs)
                        }
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<List<Balance>>> {
                return miningpoolhubApi.getUserAllBalances()
            }
        }.liveData()

    private fun loadBalances(): LiveData<Resource<List<Balance>>> =
        object : NetworkBoundResource<List<Balance>, List<Balance>>(appExecutors) {
            override fun saveCallResult(body: List<Balance>) {
                db.runInTransaction {
                    balanceDao.cleanBalances()
                    balanceDao.insertBalances(body)
                }
            }

            override fun shouldFetch(data: List<Balance>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<Balance>> {
                return object : LiveData<List<Balance>>() {
                    override fun onActive() {
                        appExecutors.diskIO.execute {
                            postValue(balanceDao.loadBalances())
                        }
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<List<Balance>>> {
                return miningpoolhubApi.getUserAllBalances()
            }
        }.liveData()

    private fun loadTicker(coin: String, convert: String): LiveData<Resource<Ticker>> =
        object : NetworkBoundResource<Ticker, List<Ticker>>(appExecutors) {
            override fun saveCallResult(body: List<Ticker>) {
                balanceDao.insertTickers(body)
                balanceDao.insertCurrencies(body.map { it.currency })
            }

            override fun shouldFetch(data: Ticker?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Ticker> {
                return object : LiveData<Ticker>() {
                    override fun onActive() {
                        appExecutors.diskIO.execute {
                            postValue(balanceDao.loadTicker(coin, convert))
                        }
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<List<Ticker>>> {
                return coinmarketcapApi.getTicker(coin, convert)
            }
        }.liveData()

    private fun loadTickers(
        coins: List<String>,
        convert: String
    ): LiveData<Resource<List<Ticker?>>> =
        object : NetworkBoundResource<List<Ticker?>, List<Ticker?>>(appExecutors) {
            override fun saveCallResult(body: List<Ticker?>) {
                balanceDao.insertTickers(body.filterNotNull())
                balanceDao.insertCurrencies(body.mapNotNull { it?.currency })
            }

            override fun shouldFetch(data: List<Ticker?>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<Ticker?>> {
                return object : LiveData<List<Ticker?>>() {
                    override fun onActive() {
                        appExecutors.diskIO.execute {
                            postValue(balanceDao.loadTickers(coins, convert))
                        }
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<List<Ticker?>>> {
                return coinmarketcapApi.getTickers(coins, convert)
            }
        }.liveData()

    fun loadBalancePairs(convert: String): LiveData<Resource<List<BalancePair>>> {
        val mediatorLiveData = MediatorLiveData<Resource<List<BalancePair>>>()
        val balancesSource = loadBalancePairsWithOldTickers(convert)
        mediatorLiveData.addSource(balancesSource) { balancesResource ->
            mediatorLiveData.postValue(balancesResource)
            if (balancesResource != null && balancesResource.status == Status.SUCCESS) {
                mediatorLiveData.removeSource(balancesSource)
                val oldData = balancesResource.data
                if (oldData != null) {
                    val coins = oldData.map { it.current.coin }
                    val tickersLive = loadTickers(coins, convert)
                    mediatorLiveData.addSource(tickersLive) { tickersResource ->
                        if (tickersResource?.status != Status.LOADING) mediatorLiveData.removeSource(tickersLive)
                        val tickers = tickersResource?.data
                        if (tickers != null) {
                            val newData = oldData.mapIndexed { index, balancePair ->
                                val ticker = tickers[index]
                                val converted = if (ticker == null) {
                                    balancePair.converted
                                } else {
                                    (balancePair.current * (ticker.convertedStats.price
                                            ?: Double.NaN))
                                        .apply { currency = ticker.convertedCurrency }
                                }
                                var current = balancePair.current
                                if (current.currency == null) {
                                    current = current.copy()
                                    current.currency = ticker?.currency
                                }
                                BalancePair(current, converted)
                            }
                            val status = tickersResource.status and balancesResource.status
                            val message = balancesResource.message ?: tickersResource.message
                            mediatorLiveData.value = Resource(
                                status = status,
                                message = message,
                                data = newData
                            )
                        }
                    }
                }
            }
        }
        return mediatorLiveData
    }

    fun cleanBalancePairs() {
        appExecutors.diskIO.execute {
            balanceDao.cleanBalances()
        }
    }
}
