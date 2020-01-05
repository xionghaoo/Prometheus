package xh.destiny.core.network

import android.content.Context
import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

import androidx.lifecycle.MediatorLiveData
import com.google.gson.Gson
import retrofit2.Response
import xh.destiny.core.utils.AppExecutors
import xh.destiny.core.vo.ApiResponse
import xh.destiny.core.vo.Resource
//import com.ks.lion.repo.data.BaseResult
import java.lang.Exception
import java.util.*

/**
 * 缓存策略，存储方式为sqlite数据库
 */
abstract class CacheRequestStrategy<T>(private val context: Context,
                                       private val appExecutors: AppExecutors
) {

    private val result: MediatorLiveData<Resource<T>> = MediatorLiveData()

    init {
        initialize()
    }

    @MainThread
    private fun initialize() {
        //先在请求结果中放一个loading状态
        result.value = Resource.loading(null)
        val dbSource = loadFromDb()
        //用result先观察本地数据库中的资料
        result.addSource(dbSource) {data ->
            result.removeSource(dbSource)
            //检查data是否需要有效，如果无效，则需要重新再从网络请求
            if (shouldFetch(data)) {
                // 本地数据不存在，从网络加载数据
                fetchFromNetwork(dbSource)
            } else {
                // 本地数据存在，直接作为加载成功的数据
                result.addSource(dbSource) { newData ->
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<T>) {
        if (!Objects.equals(result.value, newValue)) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<T>) {
        val apiResponse = createCall()
        // 网络数据加载，loading状态的数据源为数据库的数据，可以先拿来显示，等网络数据
        // 加载完成后再替换数据库中的数据
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly, maybe null
        result.addSource(dbSource) { newData -> setValue(Resource.loading(newData)) }
        //result观察网络源数据
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            // 移除loading状态的source
            result.removeSource(dbSource)

            //接收到网络请求响应
            if (response?.isSuccessful()!!) {

                onResponse(response)

                //请求成功
                appExecutors.diskIO().execute {
                    //先保存结果到数据库
                    saveCallResult(processResponse(response))
                    appExecutors.mainThread().execute {
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
                        // 再重新观察数据库的最新数据，将最新数据作为请求成功数据
                        result.addSource(loadFromDb()) { newData -> setValue(Resource.success(newData)) }
                    }
                }
            } else {
                //请求失败
                onFetchFailed(context, response.error!!)
                //设置请求失败的状态
                result.addSource(dbSource) { newData -> setValue(Resource.error(response.error!!, newData)) }
            }
        }
    }

    @WorkerThread
    protected fun processResponse(response: ApiResponse<T>): T? {
        return response.body
    }

    // Called to save the result of the API response into the database
    @WorkerThread
    protected abstract fun saveCallResult(result: T?)

    // Called with the data in the database to decide whether it should be
    // fetched from the network.
    @MainThread
    protected abstract fun shouldFetch(data: T?): Boolean

    // Called to get the cached data from the database
    @MainThread
    protected abstract fun loadFromDb(): LiveData<T>

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<T>>

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    protected abstract fun onFetchFailed(context: Context, error: String)

    // returns a LiveData that represents the resource, implemented
    // in the base class.
    fun asLiveData(): LiveData<Resource<T>> = result

    // 该方法提供请求成功时处理响应数据的机会
    protected abstract fun onResponse(response: ApiResponse<T>)

}