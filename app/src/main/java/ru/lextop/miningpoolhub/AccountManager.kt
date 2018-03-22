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
        onLoginListeners.forEach { it.invoke(login) }
    }

    fun logout() {
        privateAppPreferences.miningpoolhubApiKey.remove()
        onLoginListeners.forEach { it.invoke(null) }
    }

    fun getApiKey(): String {
        return privateAppPreferences.miningpoolhubApiKey.get()
    }

    private val onLoginListeners: MutableList<(Login?) -> Unit> = mutableListOf()
    fun addOnLoginListener(onChanged: (Login?) -> Unit) {
        onLoginListeners.add(onChanged)
    }
    fun removeOnLoginListener(onChanged: (Login?) -> Unit) {
        onLoginListeners.remove(onChanged)
    }
}
