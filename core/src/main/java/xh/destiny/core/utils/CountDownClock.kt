package xh.destiny.core.utils

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.*

/**
 * 倒计时工具
 */
class CountDownClock(private var leftTime: Long,
                     tick: (time: String?) -> Unit,
                     leftTwoMinute: () -> Unit,
                     private var stopped: () -> Unit) {

    private val handler = Handler(Looper.getMainLooper()) { msg ->
        if (msg.what == 1) {
            tick(timeStr)
        }
        return@Handler true
    }

    private var timer: Timer? = null

    private var minute: Int = 0
    private var second: Int = 0

    private var timeStr: String? = null

    private var isCalled = false

    init {
        if (timer == null) {
            timer = Timer()
            timer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    minute = (leftTime / 1000 / 60).toInt()
                    second = (leftTime / 1000 % 60).toInt()
                    val minuteStr = if (minute < 10) "0$minute" else minute
                    val secondStr = if (second < 10) "0$second" else second
                    timeStr = "$minuteStr:$secondStr"
                    handler.sendEmptyMessage(1)

                    leftTime -= 1000

                    if (leftTime <= 2 * 60 * 1000L && !isCalled && leftTime > 2 * 58 * 1000L) {
                        // 剩余两分钟
                        leftTwoMinute()
                        isCalled = true
                    }

                    if (leftTime < 0) {
                        stopped()
                    }
                }
            }, 0, 1000L)
        }
    }

    fun updateLeftTime(time: Long) {
        leftTime = time
        if (leftTime <= 0) {
            stopped()
        }
    }

    fun cancel() {
        timer?.cancel()
        timer = null
    }

}