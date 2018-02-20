package ru.lextop.miningpoolhub.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import ru.lextop.miningpoolhub.vo.Balance
import ru.lextop.miningpoolhub.vo.Ticker

@Database(
    version = 1, entities = [
        Ticker::class,
        Balance::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tickerDao(): TickerDao
    abstract fun balanceDao(): BalanceDao
}
