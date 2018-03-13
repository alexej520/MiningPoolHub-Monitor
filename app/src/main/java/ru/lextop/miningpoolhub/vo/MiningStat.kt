package ru.lextop.miningpoolhub.vo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["coin_name"], tableName = "mining_stat")
data class MiningStat(
    @SerializedName("coin_name")
    @ColumnInfo(name = "coin_name")
    val coinName: String,
    @SerializedName("host")
    @ColumnInfo(name = "host")
    val host: String,
    @SerializedName("host_list")
    @ColumnInfo(name = "host_list")
    val hostList: String,
    @SerializedName("port")
    @ColumnInfo(name = "port")
    val port: Int,
    @SerializedName("direct_mining_host")
    @ColumnInfo(name = "direct_mining_host")
    val directMiningHost: String,
    @SerializedName("direct_mining_host_list")
    @ColumnInfo(name = "direct_mining_host_list")
    val directMiningHostList: String,
    @SerializedName("direct_mining_algo_port")
    @ColumnInfo(name = "direct_mining_algo_port")
    val directMiningAlgoPort: Int,
    @SerializedName("algo")
    @ColumnInfo(name = "algo")
    val algo: String,
    @SerializedName("normalized_profit")
    @ColumnInfo(name = "normalized_profit")
    val normalizedProfit: Double,
    @SerializedName("normalized_profit_amd")
    @ColumnInfo(name = "normalized_profit_amd")
    val normalizedProfitAmd: Double,
    @SerializedName("normalized_profit_nvidia")
    @ColumnInfo(name = "normalized_profit_nvidia")
    val normalizedProfitNvidia: Double,
    @SerializedName("profit")
    @ColumnInfo(name = "profit")
    val profit: Double,
    @SerializedName("pool_hash")
    @ColumnInfo(name = "pool_hash")
    val poolHash: String,
    @SerializedName("net_hash")
    @ColumnInfo(name = "net_hash")
    val netHash: String,
    @SerializedName("difficulty")
    @ColumnInfo(name = "difficulty")
    val difficulty: Double,
    @SerializedName("reward")
    @ColumnInfo(name = "reward")
    val reward: Double,
    @SerializedName("last_block")
    @ColumnInfo(name = "last_block")
    val lastBlock: Long,
    @SerializedName("time_since_last_block")
    @ColumnInfo(name = "time_since_last_block")
    val timeSinceLastBlock: Int,
    @SerializedName("time_since_last_block_in_words")
    @ColumnInfo(name = "time_since_last_block_in_words")
    val timeSinceLastBlockInWords: String,
    @SerializedName("bittrex_buy_price")
    @ColumnInfo(name = "bittrex_buy_price")
    val bittrexBuyPrice: Double,
    @SerializedName("cryptsy_buy_price")
    @ColumnInfo(name = "cryptsy_buy_price")
    val cryptsyBuyPrice: Double,
    @SerializedName("yobit_buy_price")
    @ColumnInfo(name = "yobit_buy_price")
    val yobitBuyPrice: Double,
    @SerializedName("poloniex_buy_price")
    @ColumnInfo(name = "poloniex_buy_price")
    val poloniexBuyPrice: Double,
    @SerializedName("highest_buy_price")
    @ColumnInfo(name = "highest_buy_price")
    val highestBuyPrice: Double
)
