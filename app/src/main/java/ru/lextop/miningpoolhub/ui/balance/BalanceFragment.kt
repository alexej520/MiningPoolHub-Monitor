package ru.lextop.miningpoolhub.ui.balance

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.databinding.ItemBalanceBinding
import ru.lextop.miningpoolhub.di.Injectable
import ru.lextop.miningpoolhub.ui.common.DataBoundViewHolder
import ru.lextop.miningpoolhub.vo.Status
import javax.inject.Inject

class BalanceFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var balanceViewModel: BalanceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_balance, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        balanceViewModel =
                ViewModelProviders.of(activity!!, viewModelFactory)[BalanceViewModel::class.java]
        balanceViewModel.setConverter("RUB")
        balanceViewModel.balancePairs.observe(this, Observer {
            view?.findViewById<RecyclerView>(R.id.balance_balances)?.adapter =
                    object : RecyclerView.Adapter<DataBoundViewHolder<ItemBalanceBinding>>() {
                        val data = it?.data ?: listOf()
                        override fun onCreateViewHolder(
                            parent: ViewGroup,
                            viewType: Int
                        ): DataBoundViewHolder<ItemBalanceBinding> {
                            val binding: ItemBalanceBinding = DataBindingUtil
                                .inflate(
                                    LayoutInflater.from(parent.context),
                                    R.layout.item_balance,
                                    parent,
                                    false
                                )
                            return DataBoundViewHolder(binding)
                        }

                        override fun getItemCount(): Int {
                            return data.size
                        }

                        override fun onBindViewHolder(
                            holder: DataBoundViewHolder<ItemBalanceBinding>,
                            position: Int
                        ) {
                            holder.binding.current = data[position].converted.data
                            holder.binding.executePendingBindings()
                        }
                    }
        })
    }
}
