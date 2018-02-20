package ru.lextop.miningpoolhub.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import ru.lextop.miningpoolhub.AppExecutors
import ru.lextop.miningpoolhub.api.ApiResponse
import ru.lextop.miningpoolhub.util.setValueIfNotSame
import ru.lextop.miningpoolhub.vo.Resource
import ru.lextop.miningpoolhub.vo.Status.*

abstract class NetworkBoundResource<ResultT, RequestT>(
    private val appExecutors: AppExecutors
) {
    private val result = MediatorLiveData<Resource<ResultT>>()

    @MainThread
    fun load(): LiveData<Resource<ResultT>> {
        result.value = null
        val dbSource = loadFromDb()
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData ->
                    result.setValueIfNotSame(Resource(status = SUCCESS, data = newData!!))
                }
            }
        }
        return result
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultT>) {
        val apiResponse = createCall()
        result.addSource(dbSource) { newData ->
            result.setValueIfNotSame(Resource(status = LOADING, data = newData!!))
        }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            if (response!!.isSuccessfull) {
                appExecutors.diskIO.execute {
                    saveCallResult(processResponse(response))
                    appExecutors.mainThread.execute {
                        result.addSource(loadFromDb()) { newData ->
                            result.setValueIfNotSame(Resource(status = SUCCESS, data = newData!!))
                        }
                    }
                }
            } else {
                onFetchFailed()
                result.addSource(dbSource) { newData ->
                    result.setValueIfNotSame(
                        Resource(
                            status = ERROR,
                            message = response.errorMessage,
                            data = newData!!
                        )
                    )
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    @WorkerThread
    protected open fun processResponse(response: ApiResponse<RequestT>): RequestT {
        return response.body!!
    }

    @WorkerThread
    protected abstract fun saveCallResult(body: RequestT)

    @WorkerThread
    protected abstract fun shouldFetch(data: ResultT?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultT>

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestT>>
}
