package ru.lextop.miningpoolhub.ui.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.BR
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.databinding.FragmentLoginBinding
import ru.lextop.miningpoolhub.ui.common.DataBoundViewHolderFactory
import ru.lextop.miningpoolhub.ui.common.SimpleFactoryAdapter
import ru.lextop.miningpoolhub.vo.Login
import javax.inject.Inject

class LoginFragment : Fragment() {

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var loginViewModel: LoginViewModel

    lateinit var binding: FragmentLoginBinding

    lateinit var adapter: SimpleFactoryAdapter<Login>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.setLifecycleOwner(this)
        val logins = binding.loginLogins
        logins.addItemDecoration(DividerItemDecoration(logins.context, LinearLayoutManager.VERTICAL))
        binding.loginLogins.adapter = SimpleFactoryAdapter<Login>(
            DataBoundViewHolderFactory(R.layout.item_login, BR.login),
            appExecutors
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginViewModel =
                ViewModelProviders.of(activity!!, viewModelFactory)[LoginViewModel::class.java]

        loginViewModel.logins.observe(this, Observer {
            if (it?.data != null) {
                adapter.items = it.data
            }
        })
    }
}
