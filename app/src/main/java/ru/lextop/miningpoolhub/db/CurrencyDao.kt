package ru.lextop.miningpoolhub.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import ru.lextop.miningpoolhub.vo.Currency

@Dao
interface CurrencyDao {
    @Query("select * from currency")
    fun loadCurrencies(): LiveData<List<Currency>>
}
