package ru.lextop.miningpoolhub.vo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["id"], tableName = "currency")
data class Currency (
    @SerializedName("id")
    @ColumnInfo(name = "id")
    val id: String,
    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String,
    @SerializedName("symbol")
    @ColumnInfo(name = "symbol")
    val symbol: String
)
