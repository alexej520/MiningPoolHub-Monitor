package ru.lextop.miningpoolhub.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.lextop.miningpoolhub.ui.balance.BalanceFragment

@Module
abstract class FragmentBuildersModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeBalanceFragment(): BalanceFragment
}