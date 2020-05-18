package by.iba.sbs.library.data.remote

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

abstract class NetworkBoundResource<ResultType, RequestType> {

    private val result = MutableLiveData<Response<ResultType>>(Response.unauthorized())
    private val supervisorJob = SupervisorJob()
    private val errors = MutableLiveData<String>("")
    suspend fun build(): NetworkBoundResource<ResultType, RequestType> {
        withContext(Dispatchers.Main) {
            result.value =
                Response.loading(null)
        }
        CoroutineScope(coroutineContext).launch(supervisorJob) {
            val dbResult = loadFromDb()
            if (shouldFetch(dbResult)) {
                try {
                    fetchFromNetwork(dbResult)
                } catch (e: Exception) {
                    setValue(Response.error(e, loadFromDb()))
                }
            } else {
                setValue(Response.success(dbResult))
            }
        }
        return this
    }

    fun asLiveData() = result as LiveData<Response<ResultType>>

    // ---

    private suspend fun fetchFromNetwork(dbResult: ResultType) {
        setValue(Response.loading(dbResult)) // Dispatch latest value quickly (UX purpose)
        val apiResponse = createCallAsync().await()
        saveCallResults(processResponse(apiResponse))
        setValue(Response.success(loadFromDb()))
    }

    private fun setValue(newValue: Response<ResultType>) {
        if (result.value != newValue) result.postValue(newValue)
    }

    protected abstract fun processResponse(response: RequestType): ResultType

    protected abstract suspend fun saveCallResults(items: ResultType)

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract suspend fun loadFromDb(): ResultType

    protected abstract fun createCallAsync(): Deferred<RequestType>
}