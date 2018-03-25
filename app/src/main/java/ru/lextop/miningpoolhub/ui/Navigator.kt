package ru.lextop.miningpoolhub.ui

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.Toolbar
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
    private val viewModelFactory: ViewModelProvider.Factory
) {
    private val fragmentManager: FragmentManager = activity.supportFragmentManager

    fun openLogin() {
        fragmentManager
            .beginTransaction()
            .replace(R.id.main_fragmentContainer, LoginFragment())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack("LoginFragment")
            .commit()
    }

    fun editLoginDialog(login: Login) {
        val vm = ViewModelProviders.of(activity, viewModelFactory)[LoginDialogViewModel::class.java]
        vm.login.value = login
        fragmentManager
            .beginTransaction()
            .replace(R.id.main_fragmentContainer, LoginDialog.createEdit())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack("LoginDialog")
            .commit()
    }

    fun addLoginDialog() {
        val vm = ViewModelProviders.of(activity, viewModelFactory)[LoginDialogViewModel::class.java]
        vm.login.value = null
        fragmentManager
            .beginTransaction()
            .replace(R.id.main_fragmentContainer, LoginDialog.createAdd())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack("LoginDialog")
            .commit()
    }

    fun openBalance() {
        val vm = ViewModelProviders.of(activity, viewModelFactory)[BalanceViewModel::class.java]
        vm.retry()
        fragmentManager
            .beginTransaction()
            .replace(R.id.main_fragmentContainer, BalanceFragment.create())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack("BalanceFragment")
            .commit()
    }

    fun popBackStack() {
        if (!fragmentManager.popBackStackImmediate()) {
            fragmentManager.popBackStack()
        }
    }

    fun clearBackStack() {
        for (i in 0 until fragmentManager.backStackEntryCount) {
            fragmentManager.popBackStack()
        }
    }

    fun setupToolbarNavigationDrawer(toolbar: Toolbar) {
        activity.drawer.setToolbar(activity, toolbar)
    }

    fun setupToolbarNavigationPopBackStack(toolbar: Toolbar) {
        toolbar.setNavigationOnClickListener {
            popBackStack()
        }
    }
}
