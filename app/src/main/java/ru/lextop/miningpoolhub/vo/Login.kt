package ru.lextop.miningpoolhub.vo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity

@Entity(primaryKeys = ["api_key"], tableName = "login")
data class Login(
    @ColumnInfo(name = "api_key")
    val apiKey: String,
    @ColumnInfo(name = "name")
    val name: String
)
