/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xh.destiny.core.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Checks if a network connection exists.
 */
open class NetworkUtil {

    companion object {
        fun isNetworkConnected(context: Context?): Boolean {
            if (context == null) return false

            val connectivityManager =
                context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        }

        /**
         * 网络异常提示文本处理
         */
        fun networkError(t: Throwable?) : String {
            return if (t is UnknownHostException) {
                "网络未连接"
            } else if ((t is TimeoutException) || (t is SocketTimeoutException)) {
                "网络连接超时"
            } else {
                t?.message ?: "未知网络错误"
            }
        }
    }

}
