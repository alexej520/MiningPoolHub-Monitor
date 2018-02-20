package ru.lextop.miningpoolhub.repository

import android.arch.lifecycle.LiveData
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.api.CoinmarketcapApi
import ru.lextop.miningpoolhub.api.MiningpoolhubApi
import ru.lextop.miningpoolhub.db.BalanceDao
import ru.lextop.miningpoolhub.db.TickerDao
import ru.lextop.miningpoolhub.vo.BalancePair
import ru.lextop.miningpoolhub.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalanceRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val balanceDao: BalanceDao,
    private val tickerDao: TickerDao,
    private val miningpoolhubApi: MiningpoolhubApi,
    private val coinmarketcapApi: CoinmarketcapApi
) {
    fun loadBalances(convert: String): LiveData<Resource<List<BalancePair>>> =

}
