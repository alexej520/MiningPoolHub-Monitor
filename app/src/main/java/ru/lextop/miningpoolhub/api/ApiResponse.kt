package ru.lextop.miningpoolhub.api

class ApiResponse<T>(
    val body: T?,
    val errorMessage: String?
) {
    val isSuccessfull: Boolean
        get() = errorMessage == null
}
