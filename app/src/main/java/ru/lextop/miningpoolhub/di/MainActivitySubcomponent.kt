package ru.lextop.miningpoolhub.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import ru.lextop.miningpoolhub.ui.MainActivity

@Subcomponent(modules = [FragmentBuildersModule::class])
interface MainActivitySubcomponent : AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>()
}
