package ru.lextop.miningpoolhub.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.setValueIfNotSame(value: T) {
    if (this.value != value) {
        this.value = value
    }
}

fun <T> MutableLiveData<T>.setSameValueIfNotNull() {
    val current = value ?: return
    value = current
}

fun MutableLiveData<out CharSequence>.setSameValueIfNotNullAndNotEmpty() {
    val current = value
    if (current.isNullOrEmpty()) return
    value = current
}

fun <T> AbsentLiveData(): LiveData<T> {
    val result = MutableLiveData<T>()
    result.postValue(null)
    return result
}

fun <T> SingletonLiveData(value: T): LiveData<T> {
    val result = MutableLiveData<T>()
    result.postValue(value)
    return result
}
