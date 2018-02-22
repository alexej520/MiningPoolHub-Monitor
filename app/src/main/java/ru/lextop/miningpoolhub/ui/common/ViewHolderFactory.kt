package ru.lextop.miningpoolhub.ui.common

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlin.reflect.KClass

abstract class ViewHolderFactory<T, VH : RecyclerView.ViewHolder>(
    val itemKClass: KClass<*>
) {
    private var _viewType = 0
    val viewType: Int
        get() {
            if (_viewType == 0) {
                _viewType = System.identityHashCode(this)
            }
            return _viewType
        }

    abstract fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH
    abstract fun bindViewHolder(holder: VH, item: T)
}
