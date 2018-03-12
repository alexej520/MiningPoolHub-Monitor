package ru.lextop.miningpoolhub.ui.balance

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import ru.lextop.miningpoolhub.databinding.ItemCurrencyBinding
import ru.lextop.miningpoolhub.databinding.ItemCurrencyDropdownBinding
import ru.lextop.miningpoolhub.vo.Currency

open class CurrencyAdapter(
    context: Context
): ArrayAdapter<Currency>(context, android.R.layout.simple_spinner_item) {

    var currencies: List<Currency>? = null
        set(value) {
            field = value
            clear()
            addAll(value)
            notifyDataSetChanged()
        }

    init {
        setNotifyOnChange(false)
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    private var inflater: LayoutInflater? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val item = getItem(position)
        val view = convertView ?: ItemCurrencyBinding.inflate(inflater!!, parent, false).root
        val binding = DataBindingUtil.getBinding<ItemCurrencyBinding>(view)!!
        binding.currency = item
        binding.executePendingBindings()
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val item = getItem(position)
        val view = convertView ?: ItemCurrencyDropdownBinding.inflate(inflater!!, parent, false).root
        val binding = DataBindingUtil.getBinding<ItemCurrencyDropdownBinding>(view)!!
        binding.currency = item
        binding.executePendingBindings()
        return view
    }

    fun getDialogAdapter(): ArrayAdapter<Currency> {
        return object : CurrencyAdapter(context) {
            init {
                currencies = this@CurrencyAdapter.currencies
            }
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return getDropDownView(position, convertView, parent)
            }
        }
    }
}
