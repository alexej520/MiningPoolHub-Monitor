package ru.lextop.miningpoolhub.vo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["coin"], tableName = "balance")
data class Balance(
    @SerializedName("coin")
    @ColumnInfo(name = "coin")
    val coin: String,
    @SerializedName("confirmed")
    @ColumnInfo(name = "confirmed")
    val confirmed: Double,
    @SerializedName("unconfirmed")
    @ColumnInfo(name = "unconfirmed")
    val unconfirmed: Double,
    @SerializedName("ae_confirmed")
    @ColumnInfo(name = "ae_confirmed")
    val autoExchangeConfirmed: Double,
    @SerializedName("ae_unconfirmed")
    @ColumnInfo(name = "ae_unconfirmed")
    val autoExchangeUnconfirmed: Double,
    @SerializedName("exchange")
    @ColumnInfo(name = "exchange")
    val onExchange: Double
) {
    val total: Double get() =
        confirmed + unconfirmed + autoExchangeConfirmed + autoExchangeUnconfirmed + onExchange
    @Ignore
    var currency: Currency? = null
}
