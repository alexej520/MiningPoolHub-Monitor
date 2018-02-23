package ru.lextop.miningpoolhub.ui.login

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import ru.lextop.miningpoolhub.db.LoginDao
import ru.lextop.miningpoolhub.vo.Login
import ru.lextop.miningpoolhub.vo.Resource
import ru.lextop.miningpoolhub.vo.Status
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    loginDao: LoginDao
) : ViewModel() {
    val logins: LiveData<Resource<List<Login>>> = MediatorLiveData()

    init {
        (logins as MediatorLiveData).value = Resource(Status.LOADING)
        logins.addSource(loginDao.loadLogins()) {
            logins.postValue(Resource(Status.SUCCESS, data = it))
        }
    }
}
