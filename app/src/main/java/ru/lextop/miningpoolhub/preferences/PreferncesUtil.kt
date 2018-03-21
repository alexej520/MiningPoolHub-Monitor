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

abstract class PreferenceSet(
    val sharedPreferences: SharedPreferences
) {
    private val preferences = mutableMapOf<String, Preference>()

    fun register(preference: Preference) {
        preferences[preference.key] = preference
    }

    fun registerOnChangedListener(listener: OnChangedListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener.listener)
    }

    fun unregisterOnChangedListener(listener: OnChangedListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener.listener)
    }

    abstract class OnChangedListener(
        private val preferenceSet: PreferenceSet
    ) {
        internal val listener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                val preference = preferenceSet.preferences[key]
                if (preference != null) {
                    onChanged(preference)
                }
            }

        abstract fun onChanged(preference: Preference)
    }
}

fun PreferenceSet.registerOnChangedListener(onChanged: (Preference) -> Unit): PreferenceSet.OnChangedListener {
    val listener = object : PreferenceSet.OnChangedListener(this) {
        override fun onChanged(preference: Preference) {
            onChanged(preference)
        }
    }
    registerOnChangedListener(listener)
    return listener
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

fun PreferenceSet.intPreference(key: String, default: Int): IntPreference {
    val preference = IntPreference(sharedPreferences, key, default)
    register(preference)
    return preference
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

fun PreferenceSet.longPreference(key: String, default: Long): LongPreference {
    val preference = LongPreference(sharedPreferences, key, default)
    register(preference)
    return preference
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

fun PreferenceSet.floatPreference(key: String, default: Float): FloatPreference {
    val preference = FloatPreference(sharedPreferences, key, default)
    register(preference)
    return preference
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

fun PreferenceSet.booleanPreference(key: String, default: Boolean): BooleanPreference {
    val preference = BooleanPreference(sharedPreferences, key, default)
    register(preference)
    return preference
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

fun PreferenceSet.stringPreference(key: String, default: String): StringPreference {
    val preference = StringPreference(sharedPreferences, key, default)
    register(preference)
    return preference
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

fun PreferenceSet.stringSetPreference(key: String, default: Set<String>): StringSetPreference {
    val preference = StringSetPreference(sharedPreferences, key, default)
    register(preference)
    return preference
}

fun SharedPreferences.Editor.put(preference: StringSetPreference, value: Set<String>) {
    putStringSet(preference.key, value)
}
