package ru.lextop.miningpoolhub.ui.balance

import android.support.transition.TransitionManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.databinding.ItemBalanceBinding
import ru.lextop.miningpoolhub.ui.common.DataBoundViewHolder
import ru.lextop.miningpoolhub.ui.common.DataBoundViewHolderFactory
import ru.lextop.miningpoolhub.ui.common.SimpleFactoryAdapter
import ru.lextop.miningpoolhub.ui.common.SlideInItemAnimator
import ru.lextop.miningpoolhub.util.setVisibleOrGone

class BalanceAdapter(appExecutors: AppExecutors) : SimpleFactoryAdapter<BalanceItemViewModel>(
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
    private var expandedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        holder.itemView.setOnClickListener {
            val position = holder.layoutPosition
            TransitionManager.beginDelayedTransition(parent)
            val oldPosition = expandedPosition
            ((parent as RecyclerView).itemAnimator as SlideInItemAnimator.CommentAnimator).setAnimateMoves(false)
            if (oldPosition == position) {
                expandedPosition = RecyclerView.NO_POSITION
            } else {
                expandedPosition = position
                notifyItemChanged(oldPosition, PAYLOAD_COLLAPSE)
            }
            notifyItemChanged(position, PAYLOAD_EXPAND)
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DataBoundViewHolder<ItemBalanceBinding>).binding.root
            //.setBackgroundColor(holder.itemView.context.resources.getColor(R.color.colorPrimaryDark))
        val isExpanded = expandedPosition == position
        holder.binding.balanceDetails.setVisibleOrGone(isExpanded)
        holder.itemView.isActivated = isExpanded
        super.onBindViewHolder(holder, position)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.contains(PAYLOAD_EXPAND) || payloads.contains(PAYLOAD_COLLAPSE)) {
            holder as DataBoundViewHolder<ItemBalanceBinding>
            holder.binding.balanceDetails.setVisibleOrGone(expandedPosition == position)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun areItemsTheSame(
        item1: BalanceItemViewModel,
        item2: BalanceItemViewModel
    ): Boolean {
        val result = item1.id == item2.id
        return result
    }

    override fun areContentsTheSame(
        item1: BalanceItemViewModel,
        item2: BalanceItemViewModel
    ): Boolean {
        val result = item1 == item2
        return result
    }

    companion object {
        const val PAYLOAD_EXPAND = 1
        const val PAYLOAD_COLLAPSE = 2
    }
}
