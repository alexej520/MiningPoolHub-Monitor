package ru.lextop.miningpoolhub.ui.balance

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.databinding.FragmentBalanceBinding
import ru.lextop.miningpoolhub.di.Injectable
import ru.lextop.miningpoolhub.ui.common.SimpleFactoryAdapter
import ru.lextop.miningpoolhub.vo.Resource
import ru.lextop.miningpoolhub.vo.Status
import javax.inject.Inject

class BalanceFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var balanceViewModel: BalanceViewModel
    @Inject
    lateinit var appExecutors: AppExecutors

    lateinit var adapter: SimpleFactoryAdapter<BalanceItemViewModel>

    lateinit var binding: FragmentBalanceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_balance, container, false)
        binding.setLifecycleOwner(this)
        binding.balanceRefresh.setOnRefreshListener {
            balanceViewModel.retry()
        }
        val balances = binding.balanceBalances
        adapter = BalanceAdapter(appExecutors)
        balances.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        balances.adapter = adapter
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        balanceViewModel =
                ViewModelProviders.of(activity!!, viewModelFactory)[BalanceViewModel::class.java]
        binding.balanceViewModel = balanceViewModel
        balanceViewModel.setConverter("RUB")

        balanceViewModel.balances.observe(this, Observer {
            adapter.items = it?.data ?: return@Observer
        })

        balanceViewModel.balances.observe(
            this,
            Observer<Resource<List<BalanceItemViewModel>>> { it ->
                val refreshing = binding.balanceRefresh.isRefreshing
                val newRefreshing = it?.status == Status.LOADING
                if (refreshing != newRefreshing) {
                    binding.balanceRefresh.isRefreshing = newRefreshing
                }

                if (it?.status == Status.ERROR) {
                    snack(it.message)
                } else {
                    cleanSnack()
                }
            })
    }

    private var snackbar: Snackbar? = null

    private fun snack(text: String?) {
        val text = "No connection"
        snackbar = Snackbar.make(view!!, text, Snackbar.LENGTH_INDEFINITE)
            .setAction("RETRY") {
                balanceViewModel.retry()
            }

        snackbar!!.show()
    }

    private fun cleanSnack() {
        snackbar?.dismiss()
    }
}
