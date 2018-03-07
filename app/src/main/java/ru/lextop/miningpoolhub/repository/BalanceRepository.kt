package ru.lextop.miningpoolhub.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.api.ApiResponse
import ru.lextop.miningpoolhub.api.CoinmarketcapApi
import ru.lextop.miningpoolhub.api.MiningpoolhubApi
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
    private fun loadBalancePirsWithOldTickers(convert: String): LiveData<Resource<List<BalancePair>>> =
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
                                    val converted = current * (ticker.convertedStats.price ?: Double.NaN)
                                    converted.currency = ticker.convertedCurrency
                                    BalancePair(current, Resource(Status.LOADING, data = converted))
                                } else {
                                    BalancePair(current, Resource(Status.LOADING))
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

    fun loadBalancePairs(convert: String): LiveData<Resource<List<BalancePair>>> {
        val mediatorLiveData = MediatorLiveData<Resource<List<BalancePair>>>()
        val balancesSource = loadBalancePirsWithOldTickers(convert)
        mediatorLiveData.addSource(balancesSource) { balancesResource ->
            /*val result = if (balancesResource == null) {
                null
            } else {
                val balanceResourceStatus = balancesResource.status

                if (balanceResourceStatus == Status.ERROR || balanceResourceStatus == Status.SUCCESS) {
                    mediatorLiveData.removeSource(balancesSource)
                }

                val convertedStatus = if (balanceResourceStatus == Status.ERROR) {
                    Status.ERROR
                } else {
                    Status.LOADING
                }

                Resource(
                    status = balancesResource.status,
                    message = balancesResource.message,
                    data = balancesResource.data?.map { b ->
                        BalancePair(b, Resource(status = convertedStatus))
                    })
            }*/
            mediatorLiveData.postValue(balancesResource)
            if (balancesResource != null && balancesResource.status == Status.SUCCESS) {
                balancesResource.data?.map {
                    val coin = it.current.coin
                    val tickerSource = loadTicker(coin, convert)
                    mediatorLiveData.addSource(tickerSource) { tickerResource ->
                        if (tickerResource != null) {
                            if (tickerResource.status != Status.LOADING) {
                                mediatorLiveData.removeSource(tickerSource)
                            }
                            appExecutors.mainThread.execute {
                                val oldValue = mediatorLiveData.value
                                val newData = oldValue?.data?.map {
                                    if (it.current.coin == coin) {
                                        val ticker = tickerResource.data
                                        val convertedData = if (ticker == null) {
                                            it.converted.data
                                        } else {
                                            (it.current * (ticker.convertedStats.price ?: Double.NaN))
                                                .apply { currency = ticker.convertedCurrency }
                                        }

                                        var current = it.current
                                        if (current.currency == null) {
                                            current = current.copy()
                                            current.currency = ticker?.currency
                                        }
                                        BalancePair(
                                            current = current,
                                            converted = Resource(
                                                status = tickerResource.status,
                                                message = tickerResource.message,
                                                data = convertedData
                                            )
                                        )
                                    } else {
                                        it
                                    }
                                }
                                mediatorLiveData.value = oldValue?.copy(data = newData)
                            }
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
