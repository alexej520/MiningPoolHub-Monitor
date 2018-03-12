package ru.lextop.miningpoolhub.ui.login

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.db.LoginDao
import ru.lextop.miningpoolhub.vo.Login
import javax.inject.Inject

class LoginDialogViewModel @Inject constructor(
    private val appExecutors: AppExecutors,
    private val loginDao: LoginDao
): ViewModel() {
    val login = MutableLiveData<Login>()

    fun save(name: String, apiKey: String) {
        appExecutors.diskIO.execute {
            login.value?.let { loginDao.deleteLogin(it) }
            loginDao.insertLogin(Login(apiKey, name))
        }
    }
}
