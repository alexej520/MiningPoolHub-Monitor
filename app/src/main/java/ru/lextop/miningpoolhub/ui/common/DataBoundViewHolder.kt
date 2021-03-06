package ru.lextop.miningpoolhub.ui.common

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

open class DataBoundViewHolder<DB : ViewDataBinding>(
    val binding: DB
) : RecyclerView.ViewHolder(binding.root)
