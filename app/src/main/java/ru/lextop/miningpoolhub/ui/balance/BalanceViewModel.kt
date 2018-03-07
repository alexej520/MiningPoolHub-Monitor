package ru.lextop.miningpoolhub.ui.balance

import android.arch.lifecycle.*
import ru.lextop.miningpoolhub.repository.BalanceRepository
import ru.lextop.miningpoolhub.util.AbsentLiveData
import ru.lextop.miningpoolhub.util.setSameValueIfNotNullAndNotEmpty
import ru.lextop.miningpoolhub.util.setValueIfNotSame
import ru.lextop.miningpoolhub.vo.*
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

    val balanceTotal: LiveData<Resource<BalancePair>>

    init {
        _status.addSource(balancePairs) {
            updateStatus(it, isConverted.value ?: false)
        }
        _status.addSource(isConverted) {
            updateStatus(balancePairs.value, it ?: false)
        }

        balanceTotal = MediatorLiveData<Resource<BalancePair>>().apply {
            postValue(null)
            addSource(balancePairs) { res ->
                if (res?.data != null) {
                    val sum = res.data.fold(null as Balance?) { sum, bp ->
                        if (sum == null) bp.converted.data
                        else bp.converted.data?.let { if (it.currency != null) sum + it else null }
                                ?: sum
                    }
                    sum?.currency = sum?.currency?.copy(id = "total", name = "Total Currency")
                    if (sum != null) {
                        value = Resource(
                            res.status,
                            message = res.message,
                            data = BalancePair(sum, Resource(Status.SUCCESS, data = sum))
                        )
                    }
                }
            }
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

    fun clean() {
        balanceRepository.cleanBalancePairs()
        (balanceTotal as MutableLiveData).value = null
    }
}
