package ru.lextop.miningpoolhub.ui.common

import android.support.v7.util.DiffUtil
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.lextop.miningpoolhub.AppExecutors

abstract class SimpleAdapter<T, VH: RecyclerView.ViewHolder>(
    private val appExecutors: AppExecutors? = null
) : RecyclerView.Adapter<VH>() {
    @Suppress("UNCHECKED_CAST")
    private var inflater: LayoutInflater? = null

    private var dataVersion: Int = 0

    var items: List<T>? = null
        set(newValue) {
            val startVersion = ++dataVersion
            val oldValue = field
            if (oldValue == null) {
                if (newValue == null) {
                    return
                }
                field = newValue
                notifyDataSetChanged()
            } else if (newValue == null) {
                field = newValue
                notifyItemRangeRemoved(0, oldValue.size)
            } else if (appExecutors == null) {
                field = newValue
                notifyDataSetChanged()
            } else {
                appExecutors.workingThread.execute {
                    val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                        override fun areItemsTheSame(
                            oldItemPosition: Int,
                            newItemPosition: Int
                        ): Boolean {
                            return areItemsTheSame(
                                oldValue[oldItemPosition],
                                newValue[newItemPosition]
                            )
                        }

                        override fun getOldListSize(): Int {
                            return oldValue.size
                        }

                        override fun getNewListSize(): Int {
                            return newValue.size
                        }

                        override fun areContentsTheSame(
                            oldItemPosition: Int,
                            newItemPosition: Int
                        ): Boolean {
                            return areContentsTheSame(
                                oldValue[oldItemPosition],
                                newValue[newItemPosition]
                            )
                        }
                    })
                    appExecutors.mainThread.execute {
                        if (startVersion == dataVersion) {
                            field = newValue
                            diffResult.dispatchUpdatesTo(this)
                        }
                    }
                }
            }
        }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }
    open fun areItemsTheSame(item1: T, item2: T): Boolean {
        return false
    }

    open fun areContentsTheSame(item1: T, item2: T): Boolean {
        return false
    }
}
