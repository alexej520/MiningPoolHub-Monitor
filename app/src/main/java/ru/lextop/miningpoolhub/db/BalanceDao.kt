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
    abstract fun loadCurrency(): LiveData<List<Currency>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTickers(tickers: List<Ticker>)

    fun loadTicker(id: String, convertedSymbol: String): Ticker? {
        return loadTickerInternal(id, convertedSymbol)?.apply {
            convertedCurrency = loadCurrencyBySymbolInternal(convertedSymbol)
        }
    }

    fun loadBalances(): List<Balance> {
        val result = loadBalanceInternal()
        result.forEach {
            it.currency = loadCurrencyByIdInternal(it.coin)
        }
        return result
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertBalances(balances: List<Balance>)

    @Query("delete from balance")
    abstract fun cleanBalances()


    @Query("select * from balance order by coin asc")
    abstract fun loadBalanceInternal(): List<Balance>

    @Query("select * from currency where id=:id")
    abstract fun loadCurrencyByIdInternal(id: String): Currency?

    @Query("select * from currency where symbol=:symbol")
    abstract fun loadCurrencyBySymbolInternal(symbol: String): Currency?

    @Query("select * from ticker where id=:id and converted_symbol=:convertedSymbol")
    abstract fun loadTickerInternal(id: String, convertedSymbol: String): Ticker?
}
