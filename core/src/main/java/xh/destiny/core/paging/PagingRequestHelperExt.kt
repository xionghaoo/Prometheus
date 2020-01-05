package xh.destiny.core.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import xh.destiny.core.vo.NetworkState

private fun getErrorMessage(report: PagingRequestHelper.StatusReport): String {
    var errorMsg = PagingRequestHelper.RequestType.values().mapNotNull {
        report.getErrorFor(it)?.message
    }.first()

//    if (errorMsg == HttpHelper.ERROR_TIME_OUT) {
//        errorMsg = HttpHelper.SHOW_ERROR_TIME_OUT
//    } else if (errorMsg.contains("Unable to resolve host")) {
//        errorMsg = HttpHelper.SHOW_ERROR_UNKNOW_HOST
//    }
    return errorMsg
}

fun PagingRequestHelper.createStatusLiveData(): LiveData<NetworkState> {
    val liveData = MutableLiveData<NetworkState>()
    addListener { report ->
        when {
            report.hasRunning() -> liveData.postValue(NetworkState.LOADING)
            report.hasError() -> liveData.postValue(NetworkState.error(getErrorMessage(report)))
            else -> liveData.postValue(NetworkState.LOADED)
        }
    }
    return liveData
}
