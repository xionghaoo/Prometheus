package xh.destiny.core.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xh.destiny.core.utils.AppExecutors
import xh.destiny.core.utils.NetworkUtil
import xh.destiny.core.vo.NetworkState

abstract class PagingDataSource<R, T>(private val appExecutors: AppExecutors)
    : PageKeyedDataSource<String, T>() {

    private var retry: (() -> Any)? = null

    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    val extraData = MutableLiveData<Any>()



    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            appExecutors.networkIO().execute {
                it.invoke()
            }
        }
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, T>) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        createInitialCall().enqueue(object : Callback<R> {
            override fun onFailure(call: Call<R>, t: Throwable) {
                retry = { loadInitial(params, callback) }

                networkState.postValue(NetworkState.error(NetworkUtil.networkError(t)))
                initialLoad.postValue(NetworkState.error(NetworkUtil.networkError(t)))
                extraData.postValue(null)
            }

            override fun onResponse(call: Call<R>, response: Response<R>) {
                if (response.isSuccessful) {
                    // 数据请求成功
                    retry = null
                    callback.onResult(convertToListData(response.body()), null, "2")

                    networkState.postValue(NetworkState.LOADED)
                    initialLoad.postValue(NetworkState.LOADED)
                    extraData.postValue(response.body())

                    onResponse(response)

                } else {
                    retry = { loadInitial(params, callback) }

                    networkState.postValue(NetworkState.error(response.message()))
                    initialLoad.postValue(NetworkState.error(response.message()))
                    extraData.postValue(null)
                }
            }
        })
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, T>) {
        networkState.postValue(NetworkState.LOADING)

        createAfterCall(params.key).enqueue(object : Callback<R> {
            override fun onFailure(call: Call<R>, t: Throwable) {
                retry = { loadAfter(params, callback) }

                networkState.postValue(NetworkState.error(NetworkUtil.networkError(t)))
            }

            override fun onResponse(call: Call<R>, response: Response<R>) {
                if (response.isSuccessful) {
                    retry = null
                    callback.onResult(convertToListData(response.body()), (params.key.toInt() + 1).toString())

                    networkState.postValue(NetworkState.LOADED)

                    onResponse(response)

                } else {
                    retry = { loadAfter(params, callback) }

                    networkState.postValue(NetworkState.error(response.message()))
                }
            }
        })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, T>) {
    }

    protected abstract fun convertToListData(r: R?) : List<T>

    protected abstract fun createInitialCall() : Call<R>

    protected abstract fun createAfterCall(pageNo: String) : Call<R>

    // 该方法提供请求成功时处理响应数据的机会
    protected abstract fun onResponse(response: Response<R>)
}