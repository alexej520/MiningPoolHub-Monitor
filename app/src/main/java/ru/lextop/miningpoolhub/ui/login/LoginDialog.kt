package ru.lextop.miningpoolhub.ui.login

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.databinding.DialogLoginBinding
import ru.lextop.miningpoolhub.di.Injectable
import ru.lextop.miningpoolhub.ui.Navigator
import javax.inject.Inject

class LoginDialog : DialogFragment(), Injectable {

    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: LoginDialogViewModel

    lateinit var binding: DialogLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogLoginBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        setupToolbar(binding.toolbar)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.loginName.requestFocus()
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.loginName, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupToolbar(toolbar: Toolbar) {
        arguments?.getBoolean(ARG_ADD, true)?.let {
            binding.add = it
        }
        navigator.setupToolbarNavigationPopBackStack(toolbar)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.save -> {
                    navigator.popBackStack()
                    viewModel.save(
                        name = binding.loginName.text.toString(),
                        apiKey = binding.loginApiKey.text.toString()
                    )
                }
            }
            true
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(
            activity!!,
            viewModelFactory
        )[LoginDialogViewModel::class.java]
        binding.login = viewModel.login
    }

    companion object {
        private const val ARG_ADD = "add"
        fun createEdit(): LoginDialog {
            val dialog = LoginDialog()
            dialog.arguments = Bundle().apply {
                putBoolean(ARG_ADD, false)
            }
            return dialog
        }

        fun createAdd(): LoginDialog {
            val dialog = LoginDialog()
            dialog.arguments = Bundle().apply {
                putBoolean(ARG_ADD, true)
            }
            return dialog
        }
    }
}
