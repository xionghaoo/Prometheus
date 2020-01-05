package xh.destiny.core.vo

import android.util.Log
import retrofit2.Response
import xh.destiny.core.utils.NetworkUtil
import java.io.IOException

class ApiResponse<T> {

    public var code: Int = 0
    public var body: T? = null
    public var error: String? = null

    constructor(response: Response<T>) {
//        response = r
        code = response.code()
        if (code == 200) {
            body = response.body()
            error = null
        } else if (code >= 500) {
            error = "服务器访问错误"
        } else {
            var msg: String? = null
            if (response.errorBody() != null) {
                try {
                    msg = response.errorBody()!!.string()
                } catch (e: IOException) {
                    Log.d("ApiResponse", "error while parsing response")
                }
            }
            if (msg == null || msg.trim().isEmpty()) {
                msg = response.message()
            }
            error = msg
//            body = null
        }
    }

    constructor(t: Throwable?) {
        Log.d("ApiResponse", "error: $t")
        code = -1
        body = null
//        if (t is UnknownHostException) {
//            error = "网络未连接"
//        } else if ((t is TimeoutException) || (t is SocketTimeoutException)) {
//            error = "网络连接超时"
//        } else {
//            error = t?.message ?: "未知网络错误"
//        }
        error = NetworkUtil.networkError(t)
    }

    public fun isSuccessful() : Boolean {
        return code == 200
    }
}