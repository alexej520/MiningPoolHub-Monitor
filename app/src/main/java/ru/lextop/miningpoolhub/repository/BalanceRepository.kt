package ru.lextop.miningpoolhub.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.persistence.room.RoomDatabase
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.api.ApiResponse
import ru.lextop.miningpoolhub.api.CoinmarketcapApi
import ru.lextop.miningpoolhub.api.MiningpoolhubApi
import ru.lextop.miningpoolhub.db.AppDatabase
import ru.lextop.miningpoolhub.db.BalanceDao
import ru.lextop.miningpoolhub.db.TickerDao
import ru.lextop.miningpoolhub.util.setValueIfNotSame
import ru.lextop.miningpoolhub.vo.Balance
import ru.lextop.miningpoolhub.vo.BalancePair
import ru.lextop.miningpoolhub.vo.Resource
import ru.lextop.miningpoolhub.vo.Status.*
import ru.lextop.miningpoolhub.vo.Ticker
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
                return balanceDao.getBalances()
            }

            override fun createCall(): LiveData<ApiResponse<List<Balance>>> {
                return miningpoolhubApi.getUserAllBalances()
            }
        }.load()
}

