package ru.lextop.miningpoolhub.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.api.ApiResponse
import ru.lextop.miningpoolhub.api.CoinmarketcapApi
import ru.lextop.miningpoolhub.api.MiningpoolhubApi
import ru.lextop.miningpoolhub.db.AppDatabase
import ru.lextop.miningpoolhub.db.BalanceDao
import ru.lextop.miningpoolhub.db.CurrencyDao
import ru.lextop.miningpoolhub.db.TickerDao
import ru.lextop.miningpoolhub.vo.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalanceRepository @Inject constructor(
    private val db: AppDatabase,
    private val appExecutors: AppExecutors,
    private val balanceDao: BalanceDao,
    private val tickerDao: TickerDao,
    private val currencyDao: CurrencyDao,
    private val miningpoolhubApi: MiningpoolhubApi,
    private val coinmarketcapApi: CoinmarketcapApi
) {
    private fun loadBalances(): LiveData<Resource<List<Balance>>> =
        object : NetworkBoundResource<List<Balance>, List<Balance>>(appExecutors) {
            override fun saveCallResult(body: List<Balance>) {
                db.runInTransaction {
                    balanceDao.clean()
                    balanceDao.insert(body)
                }
            }

            override fun shouldFetch(data: List<Balance>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<Balance>> {
                return object : LiveData<List<Balance>>() {
                    override fun onActive() {
                        appExecutors.diskIO.execute {
                            val balances = balanceDao.loadBalances()
                            val currencies = currencyDao
                                .loadCurrenciesByIds(balances.map { it.coin })
                                .associateBy { it.id }
                            balances.forEach {
                                it.currency = currencies[it.coin]
                            }
                            postValue(balances)
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
                tickerDao.insert(body)
                currencyDao.insert(body.map { it.currency })
            }

            override fun shouldFetch(data: Ticker?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Ticker> {
                return object : LiveData<Ticker>() {
                    override fun onActive() {
                        appExecutors.diskIO.execute {
                            val ticker = tickerDao.loadTickerById(coin)?.apply {
                                convertedCurrency = currencyDao.loadCurrencyBySymbol(convertedSymbol)
                            }
                            postValue(ticker)
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
        val balancesSource = loadBalances()
        mediatorLiveData.addSource(balancesSource) { balancesResource ->
            val result = if (balancesResource == null) {
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
            }
            mediatorLiveData.postValue(result)
            if (balancesResource != null && balancesResource.status == Status.SUCCESS) {
                balancesResource.data?.map {
                    val coin = it.coin
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
                                        val converted =
                                            it.current * (tickerResource.data?.convertedStats?.price
                                                    ?: Double.NaN)
                                        converted.currency = tickerResource.data?.convertedCurrency
                                        it.copy(
                                            converted = Resource(
                                                status = tickerResource.status,
                                                message = tickerResource.message,
                                                data = converted
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
}
