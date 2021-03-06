package ru.lextop.miningpoolhub.vo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.annotations.JsonAdapter
import ru.lextop.miningpoolhub.util.asDoubleOrNull
import java.lang.reflect.Type

@JsonAdapter(Ticker.Deserializer::class)
@Entity(primaryKeys = ["id"], tableName = "ticker")
data class Ticker(
    @Embedded
    val currency: Currency,
    @ColumnInfo(name = "rank")
    val rank: Int,
    @Embedded(prefix = "usd")
    val usdStats: Ticker.Stats,
    @Embedded(prefix = "btc")
    val btcStats: Ticker.Stats,
    @ColumnInfo(name = "available_supply")
    val availableSupply: Double?,
    @ColumnInfo(name = "total_supply")
    val totalSupply: Double?,
    @ColumnInfo(name = "max_supply")
    val maxSupply: Double?,
    @ColumnInfo(name = "percent_change_1h")
    val percentChange1h: Double?,
    @ColumnInfo(name = "percent_change_24h")
    val percentChange24h: Double?,
    @ColumnInfo(name = "percent_change_7d")
    val percentChange7d: Double?,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Int,
    @ColumnInfo(name = "converted_symbol")
    val convertedSymbol: String,
    @Embedded(prefix = "converted")
    val convertedStats: Ticker.Stats
) {
    @Ignore
    var convertedCurrency: Currency? = null

    data class Stats(
        @ColumnInfo(name = "price")
        val price: Double?,
        @ColumnInfo(name = "volume_24h")
        val volume24h: Double?,
        @ColumnInfo(name = "market_cap")
        val marketCap: Double?
    )

    object Deserializer : JsonDeserializer<Ticker> {
        private fun deserializeStats(jsonObject: JsonObject, symbol: String): Stats {
            val price = jsonObject["price_$symbol"]?.asDoubleOrNull
            val volume24h = jsonObject["24h_volume_$symbol"]?.asDoubleOrNull
            val marketCap = jsonObject["market_cap_$symbol"]?.asDoubleOrNull
            return Stats(price = price, volume24h = volume24h, marketCap = marketCap)
        }

        override fun deserialize(
            json: JsonElement,
            type: Type,
            context: JsonDeserializationContext
        ): Ticker {
            val jsonObject = json.asJsonObject
            val currency = context.deserialize<Currency>(jsonObject, Currency::class.java)
            val rank = jsonObject["rank"].asInt
            val usdStats = deserializeStats(jsonObject, "usd")
            val btcStats = deserializeStats(jsonObject, "btc")
            val availableSupply = jsonObject["available_supply"].asDoubleOrNull
            val totalSupply = jsonObject["total_supply"].asDoubleOrNull
            val maxSupply = jsonObject["max_supply"].asDoubleOrNull
            val percentChange1h = jsonObject["percent_change_1h"].asDoubleOrNull
            val percentChange24h = jsonObject["percent_change_24h"].asDoubleOrNull
            val percentChange7d = jsonObject["percent_change_7d"].asDoubleOrNull
            val lastUpdated = jsonObject["last_updated"].asInt
            val jsonFields = jsonObject.entrySet()
            val marketCaps = jsonFields.filter { it.key.startsWith("market_cap_") }
            val otherStatsSuffix = marketCaps
                .firstOrNull { it.key != "market_cap_usd" }
                ?.key
                ?.removePrefix("market_cap_")
            val otherStats = when (otherStatsSuffix) {
                null -> usdStats
                "btc" -> btcStats
                else -> deserializeStats(jsonObject, otherStatsSuffix)
            }
            val otherSymbol = otherStatsSuffix?.toUpperCase() ?: "USD"
            return Ticker(
                currency = currency,
                rank = rank,
                usdStats = usdStats,
                btcStats = btcStats,
                availableSupply = availableSupply,
                totalSupply = totalSupply,
                maxSupply = maxSupply,
                percentChange1h = percentChange1h,
                percentChange24h = percentChange24h,
                percentChange7d = percentChange7d,
                lastUpdated = lastUpdated,
                convertedSymbol = otherSymbol,
                convertedStats = otherStats
            )
        }
    }
}
