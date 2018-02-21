package ru.lextop.miningpoolhub.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.setValueIfNotSame(value: T) {
    if (this.value != value) {
        this.value = value
    }
}

fun <T> AbsentLiveData(): LiveData<T> {
    val result = MutableLiveData<T>()
    result.postValue(null)
    return result
}
