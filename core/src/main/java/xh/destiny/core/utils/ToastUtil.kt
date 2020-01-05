package xh.destiny.core.utils

import android.content.Context
import android.widget.Toast

class ToastUtil {
    companion object {
        fun show(context: Context?, msg: String?) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}