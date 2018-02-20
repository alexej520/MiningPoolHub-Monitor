package ru.lextop.miningpoolhub.util

import android.arch.lifecycle.LiveData
import ru.lextop.miningpoolhub.api.ApiResponse
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class LiveDataCallAdapterFactory: CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>?,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != LiveData::class.java) {
            return null
        }
        val observableType: Type = getParameterUpperBound(0, returnType as ParameterizedType)
        if (getRawType(observableType) != ApiResponse::class.java) {
            return null
        }
        return LiveDataCallAdapter<ApiResponse<*>>(observableType)
    }
}