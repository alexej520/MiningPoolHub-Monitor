package ru.lextop.miningpoolhub.ui.balance

import android.arch.lifecycle.*
import ru.lextop.miningpoolhub.db.BalanceDao
import ru.lextop.miningpoolhub.preferences.AppPreferences
import ru.lextop.miningpoolhub.preferences.PreferenceSet
import ru.lextop.miningpoolhub.preferences.PrivateAppPreferences
import ru.lextop.miningpoolhub.preferences.registerOnChangedListener
import ru.lextop.miningpoolhub.repository.BalanceRepository
import ru.lextop.miningpoolhub.util.AbsentLiveData
import ru.lextop.miningpoolhub.util.setSameValueIfNotNullAndNotEmpty
import ru.lextop.miningpoolhub.util.setValueIfNotSame
import ru.lextop.miningpoolhub.vo.*
import javax.inject.Inject

class BalanceViewModel @Inject constructor(
    private val balanceRepository: BalanceRepository,
    private val balanceDao: BalanceDao,
    private val appPreferences: AppPreferences,
    private val privateAppPreferences: PrivateAppPreferences
) : ViewModel() {

    private val _converter = MutableLiveData<String>()

    val converter: LiveData<String> = _converter

    val isConverted = MutableLiveData<Boolean>()

    private val _status = MediatorLiveData<Status>()

    val status: LiveData<Status> get() = _status

    val balancePairs: LiveData<Resource<List<BalancePair>>> =
        Transformations.switchMap(_converter) { converter ->
            if (converter.isNullOrEmpty()) {
                AbsentLiveData()
            } else {
                balanceRepository.loadBalancePairs(converter)
            }
        }

    val balanceTotal: LiveData<Resource<BalancePair>>

    val privateAppPreferencesListener: PreferenceSet.OnChangedListener

    init {
        privateAppPreferencesListener = privateAppPreferences.registerOnChangedListener {
            if (it == privateAppPreferences.miningpoolhubApiKey) {
                clean()
            }
        }
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

                    val sum = res.data.fold(null as Balance?) { s, bp ->
                        val converted = bp.converted.data
                        if (converted?.currency != null) {
                            if (s == null) {
                                converted.copy().apply { currency = converted.currency }
                            } else {
                                s + converted
                            }
                        } else {
                            s
                        }
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
        setConverter(appPreferences.balanceConverter.get())
        setConverted(appPreferences.balanceIsConverted.get())
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

    val currencies: LiveData<List<Currency>> = balanceDao.loadCurrencies()

    val currencyPosition: MutableLiveData<Int> = MediatorLiveData<Int>().apply {
        addSource(currencies) { currencies ->
            if (currencies == null) {
                value = -1
            } else {
                val converter = _converter.value
                value = currencies.indexOfFirst { it.symbol == converter }
            }
        }
        observeForever {
            val currencies = currencies.value ?: return@observeForever
            setConverter(currencies[it!!].symbol)
        }
    }

    fun setConverted(converted: Boolean) {
        appPreferences.balanceIsConverted.save(converted)
        isConverted.setValueIfNotSame(converted)
    }

    private fun setConverter(converter: String) {
        appPreferences.balanceConverter.save(converter)
        this._converter.setValueIfNotSame(converter)
    }

    fun retry() {
        _converter.setSameValueIfNotNullAndNotEmpty()
    }

    fun clean() {
        balanceRepository.cleanBalancePairs()
        (balancePairs as MutableLiveData).value = null
        (balanceTotal as MutableLiveData).value = null
    }

    override fun onCleared() {
        privateAppPreferences.unregisterOnChangedListener(privateAppPreferencesListener)
    }
}
