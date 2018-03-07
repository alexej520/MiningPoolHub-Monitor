package ru.lextop.miningpoolhub.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.di.Injectable
import ru.lextop.miningpoolhub.preferences.PrivateAppPreferences
import ru.lextop.miningpoolhub.ui.balance.BalanceFragment
import ru.lextop.miningpoolhub.ui.login.LoginFragment
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector, Injectable {
    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var navigator: Navigator

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return supportFragmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            navigator.openLogin()
        }
    }
}
