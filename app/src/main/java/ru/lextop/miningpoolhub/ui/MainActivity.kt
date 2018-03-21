package ru.lextop.miningpoolhub.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import ru.lextop.miningpoolhub.AccountManager
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
    lateinit var accountManager: AccountManager
    @Inject
    lateinit var navigator: Navigator

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return supportFragmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawer {
            //translucentStatusBar = false
            //actionBarDrawerToggleEnabled = false
            accountHeader {
                profile("Home", "example@gmail.com")
            }

            primaryItem(R.string.main_balance_itemName)
            primaryItem(R.string.main_coinMining_itemName)
            primaryItem(R.string.main_autoSwitching_itemName)
        }
        if (savedInstanceState == null) {
            navigator.openLogin()
        }
    }
}
