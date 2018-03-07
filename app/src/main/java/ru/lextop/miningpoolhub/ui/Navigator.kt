package ru.lextop.miningpoolhub.ui

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.preferences.PrivateAppPreferences
import ru.lextop.miningpoolhub.ui.balance.BalanceFragment
import ru.lextop.miningpoolhub.ui.balance.BalanceViewModel
import ru.lextop.miningpoolhub.ui.login.LoginDialog
import ru.lextop.miningpoolhub.ui.login.LoginDialogViewModel
import ru.lextop.miningpoolhub.ui.login.LoginFragment
import ru.lextop.miningpoolhub.vo.Login
import javax.inject.Inject

class Navigator @Inject constructor(
    private val activity: MainActivity,
    private val viewModelFactory: ViewModelProvider.Factory,
    private val privateAppPreferences: PrivateAppPreferences
) {
    private val transitionManager: FragmentManager = activity.supportFragmentManager

    fun openLogin() {
        transitionManager
            .beginTransaction()
            .replace(R.id.main_fragmentContainer, LoginFragment())
            .commit()
    }

    fun editLoginDialog(login: Login) {
        val vm = ViewModelProviders.of(activity, viewModelFactory)[LoginDialogViewModel::class.java]
        vm.login.value = login
        transitionManager
            .beginTransaction()
            .replace(R.id.main_fragmentContainer, LoginDialog.createEdit())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack("LoginDialog")
            .commit()
    }

    fun addLoginDialog() {
        val vm = ViewModelProviders.of(activity, viewModelFactory)[LoginDialogViewModel::class.java]
        vm.login.value = null
        transitionManager
            .beginTransaction()
            .replace(R.id.main_fragmentContainer, LoginDialog.createAdd())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack("LoginDialog")
            .commit()
    }

    fun openBalance(login: Login) {
        val vm = ViewModelProviders.of(activity, viewModelFactory)[BalanceViewModel::class.java]
        if (privateAppPreferences.miningpoolhubApiKey.get() != login.apiKey) {
            vm.clean()
            privateAppPreferences.miningpoolhubApiKey.save(login.apiKey)
        }
        vm.retry()
        transitionManager
            .beginTransaction()
            .replace(R.id.main_fragmentContainer, BalanceFragment.create(login.name))
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack("BalanceFragment")
            .commit()
    }
}
