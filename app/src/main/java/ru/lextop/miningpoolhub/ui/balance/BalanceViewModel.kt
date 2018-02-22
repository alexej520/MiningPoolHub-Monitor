package ru.lextop.miningpoolhub.ui.balance

import android.arch.lifecycle.*
import ru.lextop.miningpoolhub.repository.BalanceRepository
import ru.lextop.miningpoolhub.util.AbsentLiveData
import ru.lextop.miningpoolhub.util.SingletonLiveData
import ru.lextop.miningpoolhub.util.setSameValueIfNotNullAndNotEmpty
import ru.lextop.miningpoolhub.util.setValueIfNotSame
import ru.lextop.miningpoolhub.vo.BalancePair
import ru.lextop.miningpoolhub.vo.Resource
import javax.inject.Inject

class BalanceViewModel @Inject constructor(
    private val balanceRepository: BalanceRepository
) : ViewModel() {

    private val converter = MutableLiveData<String>()

    val isConverted = MutableLiveData<Boolean>()

    private val balancePairs: LiveData<Resource<List<BalancePair>>> =
        Transformations.switchMap(converter) {
            if (it.isNullOrEmpty()) AbsentLiveData()
            else
                balanceRepository.loadBalancePairs(it)
        }

    val balances: LiveData<Resource<List<BalanceItemViewModel>>> = MediatorLiveData()

    init {
        isConverted.value = false
        (balances as MediatorLiveData).addSource(balancePairs) {
            updateBalances(it, isConverted.value ?: false)
        }
        balances.addSource(isConverted) {
            updateBalances(balancePairs.value, it ?: false)
        }
    }

    private fun updateBalances(
        balancePairs: Resource<List<BalancePair>>?,
        isConverted: Boolean
    ) {

        (balances as MutableLiveData).value = balancePairs?.let {
            Resource(status = it.status, message = it.message, data = if (isConverted) {
                it.data?.map {
                    BalanceItemViewModel(
                        it.current.coin,
                        it.current.currency,
                        it.converted.data
                    )
                }
            } else {
                it.data?.map {
                    BalanceItemViewModel(
                        it.current.coin,
                        it.current.currency,
                        it.current
                    )
                }
            })
        }
    }

    fun setConverted(converted: Boolean) {
        isConverted.setValueIfNotSame(converted)
    }

    fun setConverter(converter: String) {
        this.converter.setValueIfNotSame(converter)
    }

    fun retry() {
        converter.setSameValueIfNotNullAndNotEmpty()
    }
}
