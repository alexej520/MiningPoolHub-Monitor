package ru.lextop.miningpoolhub.ui.balance

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.databinding.library.baseAdapters.BR
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.databinding.ItemBalanceBinding
import ru.lextop.miningpoolhub.di.Injectable
import ru.lextop.miningpoolhub.ui.common.DataBoundViewHolder
import ru.lextop.miningpoolhub.ui.common.DataBoundViewHolderFactory
import ru.lextop.miningpoolhub.ui.common.SimpleFactoryAdapter
import ru.lextop.miningpoolhub.vo.Balance
import ru.lextop.miningpoolhub.vo.BalancePair
import javax.inject.Inject

class BalanceFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var balanceViewModel: BalanceViewModel
    @Inject
    lateinit var appExecutors: AppExecutors

    lateinit var adapter: SimpleFactoryAdapter<Balance>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_balance, container, false)
        val balances = view.findViewById<RecyclerView>(R.id.balance_balances)

        adapter = object : SimpleFactoryAdapter<Balance>(
            object : DataBoundViewHolderFactory<Balance, ItemBalanceBinding>(
                R.layout.item_balance,
                itemKClass = Balance::class
            ) {
                override fun bindViewHolder(
                    holder: DataBoundViewHolder<ItemBalanceBinding>,
                    item: Balance
                ) {
                    holder.binding.current = item
                }
            },
            appExecutors
        ) {
            override fun areItemsTheSame(item1: Balance, item2: Balance): Boolean {
                return item1.coin == item2.coin
            }

            override fun areContentsTheSame(item1: Balance, item2: Balance): Boolean {
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
        balanceViewModel.setConverter("RUB")

        balanceViewModel.balancePairs.observe(this, Observer {
            adapter.items = it?.data?.mapNotNull { it.current }
        })
    }
}
