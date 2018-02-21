package ru.lextop.miningpoolhub.util

import com.google.gson.JsonElement

val JsonElement.asDoubleOrNull: Double? get()  {
    if (isJsonNull) return null
    return asString.toDoubleOrNull()
}