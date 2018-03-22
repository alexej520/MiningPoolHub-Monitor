package ru.lextop.miningpoolhub.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import ru.lextop.miningpoolhub.vo.AutoSwitchingStat
import ru.lextop.miningpoolhub.vo.CoinMiningStat
import ru.lextop.miningpoolhub.vo.Currency

@Dao
abstract class StatDao {
    fun loadAutoSwithingStats(): List<AutoSwitchingStat> {
        val result = internalLoadAutoSwithingStats()
        result.forEach {
            it.currency = internalLoadCurrencyById(it.currentMiningCoin)
        }
        return result
    }

    fun loadCoinMiningStats(): List<CoinMiningStat> {
        val result = internalLoadCoinMiningStats()
        result.forEach {
            it.currency = internalLoadCurrencyById(it.coinName)
        }
        return result
    }

    @Query("select * from autoswitching_stat")
    abstract fun internalLoadAutoSwithingStats(): List<AutoSwitchingStat>

    @Query("select * from coinmining_stat")
    abstract fun internalLoadCoinMiningStats(): List<CoinMiningStat>

    @Query("select * from currency where id=:id")
    abstract fun internalLoadCurrencyById(id: String): Currency?
}
