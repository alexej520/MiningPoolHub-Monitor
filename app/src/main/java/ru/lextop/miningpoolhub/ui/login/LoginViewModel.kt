package ru.lextop.miningpoolhub.ui.login

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import ru.lextop.miningpoolhub.AccountManager
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.db.LoginDao
import ru.lextop.miningpoolhub.util.setValueIfNotSame
import ru.lextop.miningpoolhub.vo.Login
import ru.lextop.miningpoolhub.vo.Resource
import ru.lextop.miningpoolhub.vo.Status
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val appExecutors: AppExecutors,
    private val loginDao: LoginDao,
    private val accountManager: AccountManager
) : ViewModel() {
    val logins: LiveData<Resource<List<Login>>> = MediatorLiveData()

    val currentLogin: MutableLiveData<Login?> = MutableLiveData()

    init {
        (logins as MediatorLiveData).value = Resource(Status.LOADING)
        logins.addSource(loginDao.loadLogins()) { l ->
            logins.postValue(Resource(Status.SUCCESS, data = l))
            val apiKey = accountManager.getApiKey()
            currentLogin.setValueIfNotSame(l?.firstOrNull { it.apiKey == apiKey })
        }
    }

    fun remove(login: Login) {
        appExecutors.diskIO.execute {
            loginDao.deleteLogin(login)
        }
    }
}
