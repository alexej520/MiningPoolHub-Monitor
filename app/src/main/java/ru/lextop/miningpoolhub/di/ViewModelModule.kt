package ru.lextop.miningpoolhub.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.lextop.miningpoolhub.ui.balance.BalanceViewModel
import ru.lextop.miningpoolhub.ui.login.LoginDialogViewModel
import ru.lextop.miningpoolhub.ui.login.LoginViewModel
import ru.lextop.miningpoolhub.viewmodel.ViewModelFactory

@Module
interface ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(BalanceViewModel::class)
    fun bindPostViewModel(postViewModel: BalanceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginDialogViewModel::class)
    fun bindLoginDialogViewModel(loginDialogViewModel: LoginDialogViewModel): ViewModel

    @Binds
    fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}
