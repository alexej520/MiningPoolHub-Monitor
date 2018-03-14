package ru.lextop.miningpoolhub.ui.balance

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.*
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.R
import ru.lextop.miningpoolhub.databinding.FragmentBalanceBinding
import ru.lextop.miningpoolhub.di.Injectable
import ru.lextop.miningpoolhub.ui.common.SearchableSpinner
import ru.lextop.miningpoolhub.vo.*
import javax.inject.Inject

class BalanceFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var balanceViewModel: BalanceViewModel
    @Inject
    lateinit var appExecutors: AppExecutors

    lateinit var binding: FragmentBalanceBinding

    lateinit var currencyAdapter: CurrencyAdapter

    val searchableSpinnerDialogCreator = object : SearchableSpinner.DialogCreator() {
        init {
            onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(position: Int) {
                    balanceViewModel.currencyPosition.value = position
                }

                override fun onNothingSelected() {
                }
            }
        }
        override fun createAdapter(source: ArrayAdapter<*>): ArrayAdapter<*> {
            return CurrencyAdapter.getDialogAdapter(source as CurrencyAdapter)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        setupActionBar()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_balance, container, false)
        binding.setLifecycleOwner(this)

        currencyAdapter = CurrencyAdapter(context!!)
        binding.balanceRefresh.setOnRefreshListener {
            balanceViewModel.retry()
        }
        binding.balanceBalances.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        binding.balanceAdapter = BalanceAdapter(appExecutors)
        return binding.root
    }

    private fun setupActionBar() {
        val actionBar = (activity!! as AppCompatActivity).supportActionBar!!
        arguments?.getString(ARG_NAME)?.let { actionBar.title = it }
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_balance, menu)
        val currencyItem = menu!!.findItem(R.id.balance_converter)
        balanceViewModel.converter.observe(this, Observer {
            currencyItem.title = it
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity!!.onBackPressed()
            R.id.balance_isConverted -> balanceViewModel.setConverted(!balanceViewModel.isConverted.value!!)
            R.id.balance_converter -> searchableSpinnerDialogCreator.createDialog(context!!, currencyAdapter).show()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        balanceViewModel =
                ViewModelProviders.of(activity!!, viewModelFactory)[BalanceViewModel::class.java]
        binding.balanceViewModel = balanceViewModel

        balanceViewModel.balancePairs.observe(this, Observer { res ->
            if (res?.data == null) return@Observer
            binding.balanceAdapter!!.items = res.data
        })

        balanceViewModel.balanceTotal.observe(this, Observer {
            binding.balanceAdapter!!.total = it?.data ?: return@Observer
        })

        balanceViewModel.currencies.observe(this, Observer {
            currencyAdapter.currencies = it
        })

        balanceViewModel.status.observe(
            this,
            Observer<Status> { it ->
                val refreshing = binding.balanceRefresh.isRefreshing
                val newRefreshing = it == Status.LOADING
                if (refreshing != newRefreshing) {
                    binding.balanceRefresh.isRefreshing = newRefreshing
                }

                if (it == Status.ERROR) {
                    snackError()
                } else {
                    snackClean()
                }
            })
        balanceViewModel.isConverted.observe(this, Observer {
            binding.balanceAdapter!!.isConverted = it ?: false
        })
    }

    private var snackbar: Snackbar? = null

    private fun snackError() {
        snackbar = Snackbar.make(view!!, R.string.all_connectionError, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.all_retry) {
                balanceViewModel.retry()
            }

        snackbar!!.show()
    }

    private fun snackClean() {
        snackbar?.dismiss()
    }

    override fun onDetach() {
        super.onDetach()
        snackClean()
    }

    companion object {
        private const val ARG_NAME = "name"
        fun create(name: String): BalanceFragment {
            val fragment = BalanceFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_NAME, name)
            }
            return fragment
        }
    }
}
