package ru.lextop.miningpoolhub.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import ru.lextop.miningpoolhub.util.ContentValues
import ru.lextop.miningpoolhub.vo.*

@Database(
    version = 1, entities = [
        Ticker::class,
        Balance::class,
        Currency::class,
        Login::class,
        AutoSwitchingStat::class,
        CoinMiningStat::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun balanceDao(): BalanceDao
    abstract fun loginDao(): LoginDao
    abstract fun statDao(): StatDao

    object OnCreateCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            listOf(
                Currency("aud", "Australian Dollar", "AUD"),
                Currency("brl", "Brazilian Real", "BRL"),
                Currency("cad", "Canadian Dollar ", "CAD"),
                Currency("chf", "Swiss Franc", "CHF"),
                Currency("clp", "Chilean Peso", "CLP"),
                Currency("cny", "Chinese Yuan Renminbi", "CNY"),
                Currency("czk", "Czech Koruna", "CZK"),
                Currency("dkk", "Danish Krone", "DKK"),
                Currency("eur", "Euro", "EUR"),
                Currency("gbp", "British Pound", "GBP"),
                Currency("hkd", "Hong Kong Dollar", "HKD"),
                Currency("huf", "Hungarian Forint", "HUF"),
                Currency("idr", "Indonesian Rupiah", "IDR"),
                Currency("ils", "Israeli Shekel", "ILS"),
                Currency("inr", "Indian Rupee", "INR"),
                Currency("jpy", "Japanese Yen", "JPY"),
                Currency("krw", "South Korean Won", "KRW"),
                Currency("mxn", "Mexican Peso", "MXN"),
                Currency("myr", "Malaysian Ringgit", "MYR"),
                Currency("nok", "Norwegian Krone", "NOK"),
                Currency("nzd", "New Zealand Dollar", "NZD"),
                Currency("php", "Philippine Piso", "PHP"),
                Currency("pkr", "Pakistani Rupee", "PKR"),
                Currency("pln", "Polish Zloty", "PLN"),
                Currency("rub", "Russian Ruble", "RUB"),
                Currency("sek", "Swedish Krona", "SEK"),
                Currency("sgd", "Singapore Dollar", "SGD"),
                Currency("thb", "Thai Baht", "THB"),
                Currency("try", "Turkish Lira", "TRY"),
                Currency("twd", "Taiwan New Dollar", "TWD"),
                Currency("usd", "United States Dollar", "USD"),
                Currency("zar", "South African Rand", "ZAR"),
                Currency("bitcoin", "Bitcoin", "BTC")
            ).forEach {
                db.insert("currency", SQLiteDatabase.CONFLICT_REPLACE, it.toContentValues())
            }
        }

        private fun Currency.toContentValues(): ContentValues {
            return ContentValues(
                "id" to id,
                "name" to name,
                "symbol" to symbol
            )
        }
    }
}
