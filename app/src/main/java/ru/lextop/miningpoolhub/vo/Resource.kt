package ru.lextop.miningpoolhub.vo

data class Resource<T>(
    val status: Status,
    val message: String? = null,
    val data: T? = null
)
