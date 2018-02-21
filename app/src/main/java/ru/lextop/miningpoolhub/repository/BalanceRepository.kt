package ru.lextop.miningpoolhub.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.api.ApiResponse
import ru.lextop.miningpoolhub.api.CoinmarketcapApi
import ru.lextop.miningpoolhub.api.MiningpoolhubApi
import ru.lextop.miningpoolhub.db.AppDatabase
import ru.lextop.miningpoolhub.db.BalanceDao
import ru.lextop.miningpoolhub.db.TickerDao
import ru.lextop.miningpoolhub.util.AbsentLiveData
import ru.lextop.miningpoolhub.util.SingletonLiveData
import ru.lextop.miningpoolhub.vo.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalanceRepository @Inject constructor(
    private val db: AppDatabase,
    private val appExecutors: AppExecutors,
    private val balanceDao: BalanceDao,
    private val tickerDao: TickerDao,
    private val miningpoolhubApi: MiningpoolhubApi,
    private val coinmarketcapApi: CoinmarketcapApi
) {
    fun loadBalances(convert: String): LiveData<Resource<List<Balance>>> =
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
                return balanceDao.loadBalances()
            }

            override fun createCall(): LiveData<ApiResponse<List<Balance>>> {
                return miningpoolhubApi.getUserAllBalances()
            }
        }.liveData()

    fun loadTicker(convert: String): LiveData<Resource<Ticker>> =
        object : NetworkBoundResource<Ticker, List<Ticker>>(appExecutors) {
            override fun saveCallResult(body: List<Ticker>) {
                tickerDao.insert(body)
            }

            override fun shouldFetch(data: Ticker?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Ticker> {
                return tickerDao.loadTicker(convert)
            }

            override fun createCall(): LiveData<ApiResponse<List<Ticker>>> {
                return coinmarketcapApi.getTicker(convert)
            }
        }.liveData()

    fun loadBalancePairs(convert: String): LiveData<Resource<List<BalancePair>>> {
        val mediatorLiveData = MediatorLiveData<Resource<List<BalancePair>>>()
        val balancesSource = loadBalances(convert)
        mediatorLiveData.addSource(balancesSource) { balancesResource ->
            val result = if (balancesResource == null) {
                null
            } else {
                Resource(
                    status = balancesResource.status,
                    message = balancesResource.message,
                    data = balancesResource.data?.map { b ->
                        BalancePair(b, Resource(status = Status.LOADING))
                    })

            }
            mediatorLiveData.postValue(result)
            if (balancesResource?.status == Status.SUCCESS) {
                mediatorLiveData.removeSource(balancesSource)
                balancesResource.data?.map {
                    val coin = it.coin
                    val tickerSource = loadTicker(coin)
                    mediatorLiveData.addSource(tickerSource) { tickerResource ->
                        if (tickerResource != null) {
                            if (tickerResource.status == Status.SUCCESS) {
                                mediatorLiveData.removeSource(tickerSource)
                            }
                            appExecutors.mainThread.execute {
                                val oldValue = mediatorLiveData.value
                                val newData = oldValue?.data?.map {
                                    if (it.current.coin == coin) {
                                        it.copy(converted = Resource(
                                            status = tickerResource.status,
                                            message = tickerResource.message,
                                            data = it.current * (tickerResource.data?.otherStats?.price ?: Double.NaN)
                                        ))
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
