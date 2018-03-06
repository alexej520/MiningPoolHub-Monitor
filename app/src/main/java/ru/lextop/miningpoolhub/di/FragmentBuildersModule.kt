package ru.lextop.miningpoolhub.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.lextop.miningpoolhub.ui.balance.BalanceFragment
import ru.lextop.miningpoolhub.ui.login.LoginDialog
import ru.lextop.miningpoolhub.ui.login.LoginFragment

@Module
abstract class FragmentBuildersModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeBalanceFragment(): BalanceFragment

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeLoginDialog(): LoginDialog
}
