package ru.lextop.miningpoolhub.ui.balance

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import ru.lextop.miningpoolhub.repository.BalanceRepository
import ru.lextop.miningpoolhub.util.AbsentLiveData
import ru.lextop.miningpoolhub.util.setSameValueIfNotNullAndNotEmpty
import ru.lextop.miningpoolhub.util.setValueIfNotSame
import ru.lextop.miningpoolhub.vo.BalancePair
import ru.lextop.miningpoolhub.vo.Resource
import javax.inject.Inject

class BalanceViewModel @Inject constructor(
    private val balanceRepository: BalanceRepository
) : ViewModel() {

    private val converter = MutableLiveData<String>()

    val balancePairs: LiveData<Resource<List<BalancePair>>> =
        Transformations.switchMap(converter) {
            if (it.isNullOrEmpty()) AbsentLiveData()
            else
                balanceRepository.loadBalancePairs(it)
        }

    fun setConverter(converter: String) {
        this.converter.setValueIfNotSame(converter)
    }

    fun retry() {
        converter.setSameValueIfNotNullAndNotEmpty()
    }
}
