package ru.lextop.miningpoolhub.ui.common

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlin.reflect.KClass

abstract class DataBoundViewHolderFactory<T, DB : ViewDataBinding>(
    @LayoutRes
    private val layoutId: Int,
    itemKClass: KClass<*>
) : ViewHolderFactory<T, DataBoundViewHolder<DB>>(itemKClass) {
    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): DataBoundViewHolder<DB> {
        return DataBoundViewHolder(DataBindingUtil.inflate(inflater, layoutId, parent, false))
    }
}

inline fun <reified T> DataBoundViewHolderFactory(
    @LayoutRes
    layoutId: Int,
    variableId: Int
): ViewHolderFactory<T, DataBoundViewHolder<ViewDataBinding>> {
    return object : DataBoundViewHolderFactory<T, ViewDataBinding>(
        layoutId, T::class
    ) {
        override fun bindViewHolder(holder: DataBoundViewHolder<ViewDataBinding>, item: T) {
            holder.binding.setVariable(variableId, item)
        }
    }
}
