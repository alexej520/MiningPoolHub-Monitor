package ru.lextop.miningpoolhub.vo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["algo"], tableName = "autoswitch_stat")
data class AutoswitchStat(
    @SerializedName("algo")
    @ColumnInfo(name = "algo")
    val algo: String,
    @SerializedName("current_mining_coin")
    @ColumnInfo(name = "current_mining_coin")
    val currentMiningCoin: String,
    @SerializedName("host")
    @ColumnInfo(name = "host")
    val host: String,
    @SerializedName("all_host_list")
    @ColumnInfo(name = "all_host_list")
    val allHostList: String,
    @SerializedName("port")
    @ColumnInfo(name = "port")
    val port: Int,
    @SerializedName("algo_switch_port")
    @ColumnInfo(name = "algo_switch_port")
    val algoSwitchPort: Int,
    @SerializedName("multialgo_switch_port")
    @ColumnInfo(name = "multialgo_switch_port")
    val multialgoSwitchPort: Int,
    @SerializedName("profit")
    @ColumnInfo(name = "profit")
    val profit: Double,
    @SerializedName("normalized_profit_amd")
    @ColumnInfo(name = "normalized_profit_amd")
    val normalizedProfitAmd: Double,
    @SerializedName("normalized_profit_nvidia")
    @ColumnInfo(name = "normalized_profit_nvidia")
    val normalizedProfitNvidia: Double
)
