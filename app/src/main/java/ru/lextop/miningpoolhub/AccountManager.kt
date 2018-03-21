package ru.lextop.miningpoolhub

import ru.lextop.miningpoolhub.preferences.PrivateAppPreferences
import ru.lextop.miningpoolhub.vo.Login
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountManager @Inject constructor(
    private val privateAppPreferences: PrivateAppPreferences
) {
    fun isLoggedIn(): Boolean {
        return privateAppPreferences.miningpoolhubApiKey.get() ==
                privateAppPreferences.miningpoolhubApiKey.default
    }

    fun login(login: Login) {
        privateAppPreferences.miningpoolhubApiKey.save(login.apiKey)
        loginListener?.invoke(login)
    }

    fun logout() {
        privateAppPreferences.miningpoolhubApiKey.remove()
        loginListener?.invoke(null)
    }

    // null if logout
    var loginListener: ((Login?) -> Unit)? = null
}
