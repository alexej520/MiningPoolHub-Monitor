package ru.lextop.miningpoolhub.util

import android.content.ContentValues

fun ContentValues(vararg values: Pair<String, Any?>): ContentValues {
    return ContentValues().apply {
        for ((key, value) in values) {
            when (value) {
                null -> putNull(key)
                is String -> put(key, value)
                is Byte -> put(key, value)
                is Short -> put(key, value)
                is Int -> put(key, value)
                is Long -> put(key, value)
                is Float -> put(key, value)
                is Double -> put(key, value)
                is Boolean -> put(key, value)
                is ByteArray -> put(key, value)
                else -> throw IllegalArgumentException()
            }
        }
    }
}
