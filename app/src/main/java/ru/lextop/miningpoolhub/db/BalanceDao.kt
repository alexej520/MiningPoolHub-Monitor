package ru.lextop.miningpoolhub.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import ru.lextop.miningpoolhub.vo.Balance

@Dao
interface BalanceDao {
    @Query("select * from balance order by coin asc")
    fun loadBalances(): List<Balance>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(balances: List<Balance>)

    @Query("delete from balance")
    fun clean()
}
