package ru.lextop.miningpoolhub.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import ru.lextop.miningpoolhub.vo.Ticker

@Dao
interface TickerDao {
    @Query("select * from ticker where id=:id")
    fun getTicker(id: String): LiveData<Ticker?>

    @Query("select * from ticker order by rank asc")
    fun getTickers(): LiveData<List<Ticker>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tickers: Ticker)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tickers: List<Ticker>)
}
