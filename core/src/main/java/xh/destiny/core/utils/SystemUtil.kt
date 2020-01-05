package com.ks.common.utils

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.method.DigitsKeyListener
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import xh.destiny.core.utils.PermissionManager
import xh.destiny.core.utils.ToastUtil

class SystemUtil {
    companion object {
        // 隐藏软键盘
        fun hideSoftKeyboard(context: Context, editText: EditText) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }

        // 调起软键盘
        fun openSoftKeyboard(context: Context) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        // 获取状态栏高度
        fun getStatusBarHeight(resources: Resources) : Int {
            var result = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        // 复制文本到剪贴板
//        fun copyToClipboard(context: Context, str: String) {
//            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val clip = ClipData.newPlainText("Copied Text", str)
//            clipboard.primaryClip = clip
//        }

        fun setImageForegroundColor(img: ImageView, context: Context, color: Int) {
            img.setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.SRC_ATOP)
        }

        /**
         * 设置灰色状态栏
         */
        fun setDarkStatusBar(window: Window) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val decor = window.decorView
                val flags = decor.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                decor.systemUiVisibility = flags
            }
        }

        /**
         * 清除灰色状态栏
         */
        fun clearDarkStatusBar(window: Window) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val decor = window.decorView
                val flags = decor.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                decor.systemUiVisibility = flags
            }
        }

        fun setStatusBarColor(activity: Activity, window: Window, @ColorRes color: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(activity, color)
            }
        }

        fun statusBarTransparent(window: Window) {
            if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
                setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true, window)
            }
            if (Build.VERSION.SDK_INT >= 19) {
                val decor = window.decorView
                decor.systemUiVisibility = decor.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            if (Build.VERSION.SDK_INT >= 21) {
                setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false, window)
                window.statusBarColor = Color.TRANSPARENT
            }
        }

        private fun setWindowFlag(bits: Int, on: Boolean, window: Window) {
            val win = window
            val winParams = win.attributes
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }

        /**
         * 调起系统电话
         */
        fun call(context: Activity?, phoneNumber: String?) {
            if (context == null) return

            PermissionManager.checkSingle(
                activity = context,
                permission = Manifest.permission.CALL_PHONE,
                permissionName = "电话") {
                if (phoneNumber != null) {
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:" + phoneNumber)
                    context.startActivity(intent)
                } else {
                    ToastUtil.show(context, "电话号码不能为空")
                }
            }
        }

        /**
         * 调起系统短信
         */
        fun sms(context: Activity?, phone: String?, message: String?) {
            if (context == null) return

            PermissionManager.checkSingle(
                activity = context,
                permission = Manifest.permission.SEND_SMS,
                permissionName = "短信") {
                val smsToUri = Uri.parse("smsto: " + phone)
                val intent = Intent(Intent.ACTION_SENDTO, smsToUri)
                intent.putExtra("sms_body", message)
                context.startActivity(intent)
            }

        }

        /**
         * 打开设置-当前App权限设置
         */
        fun openSettingsPermission(activity: Activity?) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", activity?.packageName, null)
            intent.data = uri
            activity?.startActivity(intent)
        }

        /**
         * 打开系统设置
         */
        fun openSettings(activity: Activity?) {
            activity?.startActivityForResult(Intent(Settings.ACTION_SETTINGS), 0)
        }

        /**
         * 打开设置-位置服务
         */
        fun openSettingsLocationService(activity: Activity?) {
            activity?.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

        fun toFullScreenMode(activity: AppCompatActivity) {
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
            activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        /**
         * 限制键盘的输入
         */
        fun limitKeyboardInput(editText: EditText, allowInput: String) {
            editText.keyListener = DigitsKeyListener.getInstance("0123456789.")
        }
    }
}