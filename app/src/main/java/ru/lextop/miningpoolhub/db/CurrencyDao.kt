package ru.lextop.miningpoolhub.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import ru.lextop.miningpoolhub.vo.Currency

@Dao
interface CurrencyDao {
    @Query("select * from currency")
    fun loadCurrencies(): LiveData<List<Currency>>

    @Query("select * from currency where id in (:ids)")
    fun loadCurrenciesByIds(ids: List<String>): List<Currency>

    @Query("select * from currency where symbol=:symbol")
    fun loadCurrencyBySymbol(symbol: String): Currency?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(currency: List<Currency>)
}
