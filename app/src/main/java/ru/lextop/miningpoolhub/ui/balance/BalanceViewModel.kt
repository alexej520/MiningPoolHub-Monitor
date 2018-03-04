package ru.lextop.miningpoolhub.ui.balance

import android.arch.lifecycle.*
import ru.lextop.miningpoolhub.repository.BalanceRepository
import ru.lextop.miningpoolhub.util.AbsentLiveData
import ru.lextop.miningpoolhub.util.setSameValueIfNotNullAndNotEmpty
import ru.lextop.miningpoolhub.util.setValueIfNotSame
import ru.lextop.miningpoolhub.vo.BalancePair
import ru.lextop.miningpoolhub.vo.Resource
import ru.lextop.miningpoolhub.vo.Status
import ru.lextop.miningpoolhub.vo.and
import javax.inject.Inject

class BalanceViewModel @Inject constructor(
    private val balanceRepository: BalanceRepository
) : ViewModel() {

    private val converter = MutableLiveData<String>()

    val isConverted = MutableLiveData<Boolean>()

    private val _status = MediatorLiveData<Status>()

    val status: LiveData<Status> get() = _status

    val balancePairs: LiveData<Resource<List<BalancePair>>> =
        Transformations.switchMap(converter) {
            if (it.isNullOrEmpty()) AbsentLiveData()
            else
                balanceRepository.loadBalancePairs(it)
        }

    init {
        _status.addSource(balancePairs) {
            updateStatus(it, isConverted.value ?: false)
        }
        _status.addSource(isConverted) {
            updateStatus(balancePairs.value, it ?: false)
        }
    }

    private fun updateStatus(
        balancePairs: Resource<List<BalancePair>>?,
        isConverted: Boolean
    ) {
        var status = Status.SUCCESS
        balancePairs?.let {
            status = it.status
            if (isConverted) {
                it.data?.forEach {
                    status = status and it.converted.status
                }
            }
        }
        _status.value = status
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
