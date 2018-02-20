package ru.lextop.miningpoolhub.preferences

import android.content.Context
import android.content.SharedPreferences
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.di.AppModule
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PrivateAppPreferences @Inject constructor(
    @Named(AppModule.SHARED_PREFERENCES_PRIVATE) sharedPreferences: SharedPreferences,
    @Named(AppModule.CONTEXT_APPLICATION) context: Context
) {
    val miningpoolhubApiKey = StringPreference(
        sharedPreferences,
        context.getString(R.string.preference_key_private_miningpoolhubApiKey),
        ""
    )
}
