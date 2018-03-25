package ru.lextop.miningpoolhub.ui.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.lextop.miningpoolhub.AccountManager
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.databinding.FragmentLoginBinding
import ru.lextop.miningpoolhub.databinding.ItemLoginBinding
import ru.lextop.miningpoolhub.di.Injectable
import ru.lextop.miningpoolhub.ui.Navigator
import ru.lextop.miningpoolhub.ui.common.DataBoundViewHolder
import ru.lextop.miningpoolhub.ui.common.SimpleAdapter
import ru.lextop.miningpoolhub.vo.Login
import javax.inject.Inject

class LoginFragment : Fragment(), Injectable {

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var accountManager: AccountManager

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

        setupToolbar(binding.toolbar)

        val logins = binding.loginLogins
        logins.addItemDecoration(DividerItemDecoration(logins.context, LinearLayoutManager.VERTICAL))
        adapter = object : SimpleAdapter<Login, DataBoundViewHolder<ItemLoginBinding>>(appExecutors) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): DataBoundViewHolder<ItemLoginBinding> {
                val binding = ItemLoginBinding.inflate(inflater, parent, false)
                val holder = DataBoundViewHolder(binding)
                holder.itemView.setOnClickListener {
                    accountManager.login(items!![holder.adapterPosition])
                    navigator.openBalance()
                }
                holder.binding.loginAction.setOnClickListener {
                    val popupMenu = PopupMenu(context!!, holder.binding.loginAction)
                    popupMenu.inflate(R.menu.item_login)
                    popupMenu.setOnMenuItemClickListener { item ->
                        when(item.itemId) {
                            R.id.edit -> navigator.editLoginDialog(items!![holder.adapterPosition])
                            R.id.remove -> loginViewModel.remove(items!![holder.adapterPosition])
                            else -> return@setOnMenuItemClickListener false
                        }
                        return@setOnMenuItemClickListener true
                    }
                    popupMenu.show()
                }
                return holder
            }

            override fun onBindViewHolder(
                holder: DataBoundViewHolder<ItemLoginBinding>,
                position: Int
            ) {
                holder.binding.login = items!![position]
            }

            override fun areItemsTheSame(item1: Login, item2: Login): Boolean {
                return item1.apiKey == item2.apiKey
            }

            override fun areContentsTheSame(item1: Login, item2: Login): Boolean {
                return item1 == item2
            }
        }
        binding.loginLogins.adapter = adapter
        binding.onAdd = View.OnClickListener {
            navigator.addLoginDialog()
        }
        return binding.root
    }

    private fun setupToolbar(toolbar: Toolbar) {
        navigator.setupToolbarNavigationPopBackStack(toolbar)
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
