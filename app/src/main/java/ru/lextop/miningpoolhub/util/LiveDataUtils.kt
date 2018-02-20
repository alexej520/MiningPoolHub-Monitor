package ru.lextop.miningpoolhub.util

import android.arch.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.setValueIfNotSame(value: T) {
    if (this.value != value) {
        this.value = value
    }
}
