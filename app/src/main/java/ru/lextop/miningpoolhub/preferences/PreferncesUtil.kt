package ru.lextop.miningpoolhub.preferences

import android.content.SharedPreferences

abstract class Preference(
    val sharedPreferences: SharedPreferences,
    val key: String
) {
    abstract val default: Any
    abstract fun get(): Any
    fun remove() {
        sharedPreferences.edit()
            .remove(key)
            .apply()
    }
}

fun SharedPreferences.Editor.remove(preference: Preference) {
    remove(preference.key)
}

class IntPreference(
    sharedPreferences: SharedPreferences,
    key: String,
    override val default: Int
) : Preference(sharedPreferences, key) {
    override fun get(): Int = sharedPreferences.getInt(key, default)
    fun save(value: Int) {
        sharedPreferences.edit()
            .putInt(key, value)
            .apply()
    }
}

fun SharedPreferences.Editor.put(preference: IntPreference, value: Int) {
    putInt(preference.key, value)
}

class LongPreference(
    sharedPreferences: SharedPreferences,
    key: String,
    override val default: Long
) : Preference(sharedPreferences, key) {
    override fun get(): Long = sharedPreferences.getLong(key, default)
    fun save(value: Long) {
        sharedPreferences.edit()
            .putLong(key, value)
            .apply()
    }
}

fun SharedPreferences.Editor.put(preference: LongPreference, value: Long) {
    putLong(preference.key, value)
}

class FloatPreference(
    sharedPreferences: SharedPreferences,
    key: String,
    override val default: Float
) : Preference(sharedPreferences, key) {
    override fun get(): Float = sharedPreferences.getFloat(key, default)
    fun save(value: Float) {
        sharedPreferences.edit()
            .putFloat(key, value)
            .apply()
    }
}

fun SharedPreferences.Editor.put(preference: FloatPreference, value: Float) {
    putFloat(preference.key, value)
}

class BooleanPreference(
    sharedPreferences: SharedPreferences,
    key: String,
    override val default: Boolean
) : Preference(sharedPreferences, key) {
    override fun get(): Boolean = sharedPreferences.getBoolean(key, default)
    fun save(value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(key, value)
            .apply()
    }
}

fun SharedPreferences.Editor.put(preference: BooleanPreference, value: Boolean) {
    putBoolean(preference.key, value)
}

class StringPreference(
    sharedPreferences: SharedPreferences,
    key: String,
    override val default: String
) : Preference(sharedPreferences, key) {
    override fun get(): String = sharedPreferences.getString(key, default)
    fun save(value: String) {
        sharedPreferences.edit()
            .putString(key, value)
            .apply()
    }
}

fun SharedPreferences.Editor.put(preference: StringPreference, value: String) {
    putString(preference.key, value)
}

class StringSetPreference(
    sharedPreferences: SharedPreferences,
    key: String,
    override val default: Set<String>
) : Preference(sharedPreferences, key) {
    override fun get(): Set<String> = sharedPreferences.getStringSet(key, default)
    fun save(value: Set<String>) {
        sharedPreferences.edit()
            .putStringSet(key, value)
            .apply()
    }
}

fun SharedPreferences.Editor.put(preference: StringSetPreference, value: Set<String>) {
    putStringSet(preference.key, value)
}
