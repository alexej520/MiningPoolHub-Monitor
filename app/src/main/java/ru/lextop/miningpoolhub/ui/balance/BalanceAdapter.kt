package ru.lextop.miningpoolhub.ui.balance

import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.databinding.ItemBalanceBinding
import ru.lextop.miningpoolhub.ui.common.DataBoundViewHolder
import ru.lextop.miningpoolhub.ui.common.DataBoundViewHolderFactory
import ru.lextop.miningpoolhub.ui.common.SimpleFactoryAdapter

class BalanceAdapter(appExecutors: AppExecutors): SimpleFactoryAdapter<BalanceItemViewModel>(
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
        val result = item1.id == item2.id
        if (!result) println("${item1.id} != ${item2.id}")
        return result
    }

    override fun areContentsTheSame(
        item1: BalanceItemViewModel,
        item2: BalanceItemViewModel
    ): Boolean {
        val result = item1 == item2
        if (!result) println("$item1 != $item2")
        return result
    }
}
