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
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.databinding.FragmentLoginBinding
import ru.lextop.miningpoolhub.databinding.ItemLoginBinding
import ru.lextop.miningpoolhub.ui.common.DataBoundViewHolder
import ru.lextop.miningpoolhub.ui.common.SimpleAdapter
import ru.lextop.miningpoolhub.vo.Login
import javax.inject.Inject

class LoginFragment : Fragment() {

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var loginViewModel: LoginViewModel

    lateinit var binding: FragmentLoginBinding

    lateinit var adapter: SimpleAdapter<Login, DataBoundViewHolder<ItemLoginBinding>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.setLifecycleOwner(this)
        val logins = binding.loginLogins
        logins.addItemDecoration(DividerItemDecoration(logins.context, LinearLayoutManager.VERTICAL))
        adapter = object : SimpleAdapter<Login, DataBoundViewHolder<ItemLoginBinding>>(appExecutors) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): DataBoundViewHolder<ItemLoginBinding> {
                val binding = ItemLoginBinding.inflate(inflater, parent, false)
                return DataBoundViewHolder(binding)
            }

            override fun onBindViewHolder(
                holder: DataBoundViewHolder<ItemLoginBinding>,
                position: Int
            ) {
                holder.binding.login = items!![position]
            }
        }
        binding.loginLogins.adapter = adapter
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
