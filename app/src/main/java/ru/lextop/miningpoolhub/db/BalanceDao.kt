package ru.lextop.miningpoolhub.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import ru.lextop.miningpoolhub.vo.Balance
import ru.lextop.miningpoolhub.vo.Currency
import ru.lextop.miningpoolhub.vo.Ticker

@Dao
abstract class BalanceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertCurrencies(currency: List<Currency>)

    @Query("select * from currency")
    abstract fun loadCurrencies(): LiveData<List<Currency>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTickers(tickers: List<Ticker>)

    fun loadTicker(id: String, convertedSymbol: String): Ticker? {
        return internalLoadTicker(id, convertedSymbol)?.apply {
            convertedCurrency = internalLoadCurrencyBySymbol(convertedSymbol)
        }
    }

    fun loadTickers(ids: List<String>, convertedSymbol: String): List<Ticker?> {
        return ids.map { id ->
            loadTicker(id, convertedSymbol)
        }
    }

    fun loadBalances(): List<Balance> {
        val result = internalLoadBalances()
        result.forEach {
            it.currency = internalLoadCurrencyById(it.coin)
        }
        return result
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertBalances(balances: List<Balance>)

    @Query("delete from balance")
    abstract fun cleanBalances()


    @Query("select * from balance order by coin asc")
    abstract fun internalLoadBalances(): List<Balance>

    @Query("select * from currency where id=:id")
    abstract fun internalLoadCurrencyById(id: String): Currency?

    @Query("select * from currency where symbol=:symbol")
    abstract fun internalLoadCurrencyBySymbol(symbol: String): Currency?

    @Query("select * from ticker where id=:id and converted_symbol=:convertedSymbol")
    abstract fun internalLoadTicker(id: String, convertedSymbol: String): Ticker?

    /*@Query("select * from ticker where id in")
    abstract fun internalLoadTickers()*/
}
