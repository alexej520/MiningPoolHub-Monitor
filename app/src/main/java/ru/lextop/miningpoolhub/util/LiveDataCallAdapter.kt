package ru.lextop.miningpoolhub.util

import android.arch.lifecycle.LiveData
import ru.lextop.miningpoolhub.api.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapter<T>(
    private val responseType: Type
): CallAdapter<ApiResponse<T>, LiveData<ApiResponse<T>>> {
    override fun adapt(call: Call<ApiResponse<T>>): LiveData<ApiResponse<T>> {
        return object : LiveData<ApiResponse<T>>() {
            private val started = AtomicBoolean()
            override fun onActive() {
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<ApiResponse<T>> {
                        override fun onFailure(call: Call<ApiResponse<T>>, t: Throwable) {
                            postValue(ApiResponse(body = null, errorMessage = t.message))
                        }

                        override fun onResponse(
                            call: Call<ApiResponse<T>>,
                            response: Response<ApiResponse<T>>
                        ) {
                            if (response.isSuccessful) {
                                postValue(response.body())
                            } else {
                                val errorMessage = try {
                                    response.errorBody()?.string()
                                } catch (e: IOException) {
                                    null
                                }.takeUnless { it.isNullOrEmpty() } ?: response.message()
                                postValue(ApiResponse(body = null, errorMessage = errorMessage))
                            }
                        }
                    })
                }
            }
        }
    }
    override fun responseType(): Type = responseType
}