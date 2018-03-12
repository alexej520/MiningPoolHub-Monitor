package ru.lextop.miningpoolhub.ui.common

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.content.res.TypedArray
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatSpinner
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import ru.lextop.miningpoolhub.R

class SearchableSpinner(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    mode: Int,
    popupTheme: Resources.Theme?
) : AppCompatSpinner(context, attrs, defStyleAttr, mode, popupTheme) {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, mode: Int) : this(
        context,
        null,
        android.support.v7.appcompat.R.attr.spinnerStyle,
        mode
    )

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        android.support.v7.appcompat.R.attr.spinnerStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        -1
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        mode: Int
    ) : this(context, attrs, defStyleAttr, mode, null)

    private val mode: Int

    init {
        var aa: TypedArray? = null
        var mode = mode
        try {
            aa = context.obtainStyledAttributes(attrs, ATTRS_ANDROID_SPINNERMODE, defStyleAttr, 0)
            if (aa!!.hasValue(0)) {
                 mode = aa.getInt(0, MODE_DIALOG)
            }
        } catch (e: Exception) {

        } finally {
            aa?.recycle()
        }
        this.mode = mode
    }

    override fun performClick(): Boolean {
        if (mode == MODE_DIALOG) {
            if (!isDialogShow) {
                createDialog().show()
                isDialogShow = true
            }
            return true
        }
        return super.performClick()
    }

    var getDialogAdapter: ((ArrayAdapter<*>) -> ArrayAdapter<*>)? = null

    private var isDialogShow = false

    private fun createDialog(): Dialog {
        val ctx = popupContext ?: context
        val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_searchable_spinner, null)
        val searchView = view.findViewById<SearchView>(R.id.searchableSpinner_search)
        val itemsListView = view.findViewById<ListView>(R.id.searchableSpinner_items)
        itemsListView.isTextFilterEnabled = true
        val adapter = getDialogAdapter?.invoke(adapter as ArrayAdapter<*>) ?: adapter as ArrayAdapter<*>
        val selection = selectedItemPosition

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                adapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        itemsListView.adapter = adapter
        val dialog = AlertDialog.Builder(ctx)
            .setView(view)
            .setPositiveButton(android.R.string.cancel, null)
            .create()

        val onDismissListener = object: DialogInterface.OnDismissListener {
            private var isItemSelect = false
            private var item: Any? = null

            fun selectItem(item: Any?) {
                isItemSelect = true
                this.item = item
            }

            override fun onDismiss(dialog: DialogInterface?) {
                if (!isItemSelect) {
                    adapter.filter.filter(null) {
                        setSelection(selection)
                    }
                } else {
                    adapter.filter.filter(null) {
                        for (i in 0 until adapter.count) {
                            if (adapter.getItem(i) == item) {
                                setSelection(i)
                                break
                            }
                        }
                    }
                }
                isDialogShow = false
            }
        }

        dialog.setOnDismissListener(onDismissListener)
        itemsListView.setOnItemClickListener { _, _, position, id ->
            val item = adapter.getItem(position)
            onDismissListener.selectItem(item)
            dialog.dismiss()
        }
        return dialog
    }

    companion object {
        val ATTRS_ANDROID_SPINNERMODE = intArrayOf(android.R.attr.spinnerMode)
    }
}