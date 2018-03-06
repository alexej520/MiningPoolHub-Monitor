package ru.lextop.miningpoolhub.ui.balance

import android.annotation.SuppressLint
import android.support.transition.AutoTransition
import android.support.transition.Transition
import android.support.transition.TransitionManager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.databinding.ItemBalanceBinding
import ru.lextop.miningpoolhub.ui.common.SimpleAdapter
import ru.lextop.miningpoolhub.vo.Balance
import ru.lextop.miningpoolhub.vo.BalancePair
import ru.lextop.miningpoolhub.vo.Currency

class BalanceAdapter(appExecutors: AppExecutors) :
    SimpleAdapter<BalancePair, BalanceAdapter.BalancePairViewHolder>(
        appExecutors
    ) {
    private var expandedPosition = RecyclerView.NO_POSITION

    var total: BalancePair? = null
        set(value) {
            val oldField = field
            if (oldField == value) return
            val count = itemCount
            field = value
            if (value == null) {
                if (oldField != null) {
                    notifyItemInserted(count)
                }
            } else {
                if (oldField != null) {
                    notifyItemChanged(count - 1)
                } else {
                    notifyItemRemoved(count - 1)
                }
            }
        }

    private val itemAnimator = ItemAnimator()
    private val touchAbsorber = View.OnTouchListener { _, _ -> true }
    private val transition = AutoTransition()
    private var transitionListener: Transition.TransitionListener? = null
    private var layoutInflater: LayoutInflater? = null

    override fun getItemCount(): Int {
        var count = super.getItemCount()
        if (total != null) count++
        return count
    }

    var isConverted = false
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount, PAYLOAD_CONVERTED)
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.itemAnimator = itemAnimator
        transitionListener = object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                itemAnimator.animateMoves = true
                recyclerView.setOnTouchListener(null)
            }

            override fun onTransitionResume(transition: Transition) {}
            override fun onTransitionPause(transition: Transition) {}
            override fun onTransitionCancel(transition: Transition) {}

            override fun onTransitionStart(transition: Transition) {
                recyclerView.setOnTouchListener(touchAbsorber)
            }
        }
        transition.addListener(transitionListener!!)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setOnTouchListener(null)
        transition.removeListener(transitionListener!!)
        transitionListener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalancePairViewHolder {
        if (layoutInflater == null) layoutInflater = LayoutInflater.from(parent.context)

        val holder =
            BalancePairViewHolder(ItemBalanceBinding.inflate(layoutInflater!!, parent, false))

        holder.itemView.setOnClickListener {
            val position = holder.layoutPosition
            TransitionManager.beginDelayedTransition(parent, transition)
            val oldPosition = expandedPosition
            itemAnimator.animateMoves = false
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

    override fun onBindViewHolder(holder: BalancePairViewHolder, position: Int) {
        holder.showDetails(expandedPosition == position)
    }

    override fun onBindViewHolder(
        holder: BalancePairViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val total = total
        val item: BalancePair
        if (position == itemCount - 1 && total != null) {
            holder.itemView.setBackgroundColor(holder.itemView.context.resources.getColor(android.R.color.darker_gray))
            item = total
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.context.resources.getColor(android.R.color.white))
            item = items!![position]
        }

        if (payloads.contains(PAYLOAD_EXPAND) || payloads.contains(PAYLOAD_COLLAPSE)) {
            holder.showDetails(expandedPosition == position)
        } else {
            holder.bindBalance(if (isConverted) item.converted.data else item.current)
            if (!payloads.contains(PAYLOAD_CONVERTED)) {
                holder.bindCurrency(item.current.currency)
            }
        }
    }

    override fun areItemsTheSame(
        item1: BalancePair,
        item2: BalancePair
    ): Boolean {
        val result = item1.current.coin == item2.current.coin
        return result
    }

    override fun areContentsTheSame(
        item1: BalancePair,
        item2: BalancePair
    ): Boolean {
        val result = if (isConverted) {
            item1.converted.data == item2.converted.data
        } else {
            item1.current == item2.current
        }
        return result
    }

    companion object {
        const val PAYLOAD_EXPAND = 1
        const val PAYLOAD_COLLAPSE = 2
        const val PAYLOAD_CONVERTED = 3
    }


    private class ItemAnimator : DefaultItemAnimator() {
        var animateMoves = false

        override fun animateMove(
            holder: RecyclerView.ViewHolder?,
            fromX: Int,
            fromY: Int,
            toX: Int,
            toY: Int
        ): Boolean {
            return if (!animateMoves) {
                dispatchMoveFinished(holder)
                false
            } else {
                super.animateMove(holder, fromX, fromY, toX, toY)
            }
        }
    }

    class BalancePairViewHolder(
        private val binding: ItemBalanceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindCurrency(currency: Currency?) {
            binding.currency = currency
            binding.executePendingBindings()
        }

        fun bindBalance(balance: Balance?) {
            binding.balance = balance
            binding.executePendingBindings()
        }

        fun showDetails(showDetails: Boolean) {
            binding.showDetails = showDetails
            binding.executePendingBindings()
        }
    }
}
