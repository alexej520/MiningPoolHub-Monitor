package ru.lextop.miningpoolhub.ui.balance

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.databinding.FragmentBalanceBinding
import ru.lextop.miningpoolhub.databinding.ItemBalanceBinding
import ru.lextop.miningpoolhub.di.Injectable
import ru.lextop.miningpoolhub.ui.common.DataBoundViewHolder
import ru.lextop.miningpoolhub.ui.common.DataBoundViewHolderFactory
import ru.lextop.miningpoolhub.ui.common.SimpleFactoryAdapter
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
        val view = binding.root
        binding.balanceRefresh.setOnRefreshListener {
            balanceViewModel.retry()
        }

        val balances = view.findViewById<RecyclerView>(R.id.balance_balances)
        balances.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        adapter = object : SimpleFactoryAdapter<BalanceItemViewModel>(
            object : DataBoundViewHolderFactory<BalanceItemViewModel, ItemBalanceBinding>(
                R.layout.item_balance,
                itemKClass = BalanceItemViewModel::class
            ) {
                override fun bindViewHolder(
                    holder: DataBoundViewHolder<ItemBalanceBinding>,
                    item: BalanceItemViewModel
                ) {
                    holder.binding.currency = item.currency
                    holder.binding.balance = item.balance
                }
            },
            appExecutors
        ) {
            override fun areItemsTheSame(
                item1: BalanceItemViewModel,
                item2: BalanceItemViewModel
            ): Boolean {
                return item1.id == item2.id
            }

            override fun areContentsTheSame(
                item1: BalanceItemViewModel,
                item2: BalanceItemViewModel
            ): Boolean {
                return item1 == item2
            }
        }

        balances.adapter = adapter
        return view
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

        balanceViewModel.balances.observe(this, Observer {
            val refreshing = binding.balanceRefresh.isRefreshing
            val newRefreshing = it?.status == Status.LOADING
            if (refreshing != newRefreshing) {
                binding.balanceRefresh.isRefreshing = newRefreshing
            }
        })
    }
}
