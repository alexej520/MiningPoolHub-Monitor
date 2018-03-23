package ru.lextop.miningpoolhub.ui

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.profile.ProfileDrawerItemKt
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.model.AbstractDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import ru.lextop.miningpoolhub.AccountManager
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.di.Injectable
import ru.lextop.miningpoolhub.preferences.PrivateAppPreferences
import ru.lextop.miningpoolhub.ui.balance.BalanceFragment
import ru.lextop.miningpoolhub.ui.login.LoginDialogViewModel
import ru.lextop.miningpoolhub.ui.login.LoginFragment
import ru.lextop.miningpoolhub.ui.login.LoginViewModel
import ru.lextop.miningpoolhub.vo.Login
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector, Injectable {
    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var accountManager: AccountManager
    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var loginViewModel: LoginViewModel
    @Inject
    lateinit var loginDialogViewModel: LoginDialogViewModel

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return supportFragmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel =
                ViewModelProviders.of(this, viewModelFactory)[LoginViewModel::class.java]
        loginDialogViewModel =
                ViewModelProviders.of(this, viewModelFactory)[LoginDialogViewModel::class.java]
        setContentView(R.layout.activity_main)
        val supportToolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(supportToolbar)
        lateinit var accountHeader: AccountHeader
        drawer {
            //translucentStatusBar = false
            //actionBarDrawerToggleEnabled = false
            toolbar = supportToolbar
            closeOnClick = true
            accountHeader = accountHeader {
                backgroundDrawable = ColorDrawable(resources.getColor(android.R.color.darker_gray))
                closeOnClick = true
                delayOnDrawerClose = 500
                onProfileChanged { _, profile, current ->
                    when (profile.identifier.toInt()) {
                        R.id.main_addLogin -> {
                            navigator.addLoginDialog()
                        }
                        R.id.main_manageLogins -> {
                            navigator.openLogin()
                        }
                        R.id.main_login -> {
                            if (!current) {
                                navigator.clearBackStack()
                                val login = (profile as AbstractDrawerItem<*, *>).tag as Login
                                accountManager.login(login)
                                navigator.openBalance()
                            }
                        }
                        R.id.main_addFirstLogin -> {
                            navigator.addLoginDialog()
                        }
                    }
                    false
                }
            }

            primaryItem(R.string.main_balance_itemName) {
                onClick { _ ->
                    navigator.openBalance()
                    false
                }
            }
            primaryItem(R.string.main_coinMining_itemName)
            primaryItem(R.string.main_autoSwitching_itemName)
        }

        loginViewModel.logins.observe(this, Observer { resource ->
            val logins = resource?.data ?: return@Observer
            accountHeader.clear()
            val apiKey = accountManager.getApiKey()
            var selectedProfile: ProfileDrawerItem? = null
            logins.forEachIndexed { id, login ->
                val profile = ProfileDrawerItem()
                    .withName(login.name)
                    .withEmail(login.apiKey)
                    .withNameShown(true)
                    .withIdentifier(R.id.main_login.toLong())
                    .withTag(login)
                if (login.apiKey == apiKey) {
                    selectedProfile = profile
                }
                accountHeader.addProfile(profile, id)
            }
            if (selectedProfile == null) {
                selectedProfile = ProfileDrawerItem()
                    .withName(R.string.add)
                    .withEmail("")
                    .withIcon(R.drawable.ic_add_white_24dp)
                    .withNameShown(true)
                    .withIdentifier(R.id.main_addFirstLogin.toLong())
                accountHeader.addProfile(selectedProfile!!, logins.size)
            }
            accountHeader.setActiveProfile(selectedProfile, false)
            accountHeader.addProfile(
                ProfileSettingDrawerItem()
                    .withName(R.string.main_addLogin_menuItem)
                    .withIcon(resources.getDrawable(R.drawable.ic_add_gray_24dp))
                    .withIdentifier(R.id.main_addLogin.toLong()),
                accountHeader.profiles.size
            )
            accountHeader.addProfile(
                ProfileSettingDrawerItem()
                    .withName(R.string.main_manageLogins_menuItem)
                    .withIcon(resources.getDrawable(R.drawable.ic_settings_gray_24dp))
                    .withIdentifier(R.id.main_manageLogins.toLong()),
                accountHeader.profiles.size
            )
        })

        if (savedInstanceState == null) {
            navigator.openBalance()
        }
    }
}
