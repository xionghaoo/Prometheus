package xh.destiny.core.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import retrofit2.Call
import retrofit2.Response
import xh.destiny.core.utils.AppExecutors

abstract class PagingDataFactory<R, T>(private val appExecutors: AppExecutors)
    : DataSource.Factory<String, T>() {

    val sourceLiveData = MutableLiveData<PagingDataSource<R, T>>()

    override fun create(): DataSource<String, T> {
        val source = object : PagingDataSource<R, T>(appExecutors) {
            override fun convertToListData(r: R?): List<T> =
                this@PagingDataFactory.convertToListData(r)

            override fun createInitialCall(): Call<R> =
                this@PagingDataFactory.createInitialCall()

            override fun createAfterCall(pageNo: String): Call<R> =
                this@PagingDataFactory.createAfterCall(pageNo)

            override fun onResponse(response: Response<R>) {
                this@PagingDataFactory.onResponse(response)
            }
        }
        sourceLiveData.postValue(source)
        return source
    }

    protected abstract fun convertToListData(r: R?) : List<T>

    protected abstract fun createInitialCall() : Call<R>

    protected abstract fun createAfterCall(pageNo: String) : Call<R>

    // 该方法提供请求成功时处理响应数据的机会
    protected abstract fun onResponse(response: Response<R>)
}