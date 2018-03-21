package ru.lextop.miningpoolhub.preferences

import android.content.Context
import android.content.SharedPreferences
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.di.AppModule
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @Named(AppModule.SHARED_PREFERENCES_APP) sharedPreferences: SharedPreferences,
    @Named(AppModule.CONTEXT_APPLICATION) context: Context
): PreferenceSet(sharedPreferences) {
    val balanceConverter = stringPreference(
        context.getString(R.string.preference_key_app_balanceConverter),
        "USD"
    )

    val balanceIsConverted = booleanPreference(
        context.getString(R.string.preference_key_app_balanceIsConverted),
        false
    )
}
