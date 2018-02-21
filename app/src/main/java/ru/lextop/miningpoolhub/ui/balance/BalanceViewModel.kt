package ru.lextop.miningpoolhub.ui.balance

import android.arch.lifecycle.*
import ru.lextop.miningpoolhub.repository.BalanceRepository
import ru.lextop.miningpoolhub.util.AbsentLiveData
import ru.lextop.miningpoolhub.vo.Balance
import ru.lextop.miningpoolhub.vo.Resource
import javax.inject.Inject

class BalanceViewModel @Inject constructor(
    private val balanceRepository: BalanceRepository
) : ViewModel() {
    private val converter = MutableLiveData<String>()
    val balances: LiveData<Resource<List<Balance>>> = Transformations.switchMap(converter) {
        if (it.isEmpty()) AbsentLiveData()
        else balanceRepository.loadBalances(it)
    }

    fun setConverter(converter: String) {
        this.converter.value = converter
    }
}
