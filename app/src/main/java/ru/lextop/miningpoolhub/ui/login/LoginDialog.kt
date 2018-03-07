package ru.lextop.miningpoolhub.ui.login

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.databinding.DialogLoginBinding
import ru.lextop.miningpoolhub.di.Injectable
import javax.inject.Inject

class LoginDialog : DialogFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: LoginDialogViewModel

    lateinit var binding: DialogLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupActionBar()
        setHasOptionsMenu(true)

        binding = DialogLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.loginName.requestFocus()
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.loginName, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupActionBar() {
        val actionBar = (activity!! as AppCompatActivity).supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.dialog_login, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity!!.onBackPressed()
            R.id.save -> {
                viewModel.save(
                    name = binding.loginName.text.toString(),
                    apiKey = binding.loginApiKey.text.toString()
                )
                activity!!.onBackPressed()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(
            activity!!,
            viewModelFactory
        )[LoginDialogViewModel::class.java]
        binding.login = viewModel.login
    }
}
