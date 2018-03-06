package ru.lextop.miningpoolhub.ui.login

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.di.Injectable

class LoginDialog: DialogFragment(), Injectable {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        (activity!! as AppCompatActivity)
            .supportActionBar!!.apply {
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
            setDisplayHomeAsUpEnabled(true)
        }
        return inflater.inflate(R.layout.dialog_login, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.dialog_login, menu)
    }
}
