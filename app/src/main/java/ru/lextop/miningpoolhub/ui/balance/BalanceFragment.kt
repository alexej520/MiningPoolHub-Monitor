package ru.lextop.miningpoolhub.ui.balance

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.lextop.miningpoolhub.di.Injectable
import ru.lextop.miningpoolhub.vo.Status
import javax.inject.Inject

class BalanceFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var balanceViewModel: BalanceViewModel

    lateinit var tw: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container == null) return null
        tw = TextView(activity)
        return tw
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        balanceViewModel =
                ViewModelProviders.of(activity!!, viewModelFactory)[BalanceViewModel::class.java]
        balanceViewModel.setConverter("RUB")
        balanceViewModel.balancePairs.observe(this, Observer {
            if (it?.status == Status.SUCCESS) {
                val sum = it.data?.sumByDouble {
                    it.converted.let {
                        if (it.status == Status.SUCCESS) {
                            with (it.data!!) {
                                confirmed + unconfirmed + autoExchangeConfirmed + autoExchangeUnconfirmed + onExchange
                            }
                        } else {
                            0.0
                        }
                    }
                }
                tw.text = "RUB: $sum"
            }
        })
        tw.setOnClickListener{
            balanceViewModel.setConverter("USD")
        }
    }
}
