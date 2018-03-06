package ru.lextop.miningpoolhub.ui

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.ui.login.LoginDialog
import ru.lextop.miningpoolhub.ui.login.LoginFragment
import javax.inject.Inject

class Navigator @Inject constructor(
    activity: MainActivity
) {
    private val transitionManager: FragmentManager = activity.supportFragmentManager

    fun openLogin() {
        transitionManager
            .beginTransaction()
            .replace(R.id.main_fragmentContainer, LoginFragment())
            .commit()
    }

    fun openLoginDialog() {
        transitionManager
            .beginTransaction()
            .replace(R.id.main_fragmentContainer, LoginDialog())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack("LoginDialog")
            .commit()
    }
}
