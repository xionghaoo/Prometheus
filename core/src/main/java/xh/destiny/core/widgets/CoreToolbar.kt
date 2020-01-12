package com.ks.horse.widgets

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.ks.common.utils.SystemUtil
import kotlinx.android.synthetic.main.core_widget_toolbar.view.*
import xh.destiny.core.R
import xh.destiny.core.utils.ResourceUtil

class CoreToolbar : LinearLayout {

//    var tabLayout: SmartTabLayout? = null

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        inflate(context, R.layout.core_widget_toolbar, this)

        isFocusable = true
        isClickable = true
        orientation = VERTICAL

        var ta: TypedArray? = null
        try {
            ta = context.theme.obtainStyledAttributes(attrs, R.styleable.CoreToolbar, 0, 0)
            val hasStatusBar = ta.getBoolean(R.styleable.CoreToolbar_core_hasStatusBar, true)
            val hasBackButton = ta.getBoolean(R.styleable.CoreToolbar_core_hasBack, false)
//            val hasCloseButton = ta.getBoolean(R.styleable.XToolbar_hasCloseButton, false)
            val hasBottomLine = ta.getBoolean(R.styleable.CoreToolbar_core_hasBottomLine, true)
//            val isWhite = ta.getBoolean(R.styleable.XToolbar_isWhiteStyle, false)
            val hasElevation = ta.getBoolean(R.styleable.CoreToolbar_core_hasElevation, false)
//            val hasCategory = ta.getBoolean(R.styleable.XToolbar_hasCategoryMenu, false)
//            val hasDrawerSwitch = ta.getBoolean(R.styleable.XToolbar_hasDrawerSwitch, false)
//            val hasSettings = ta.getBoolean(R.styleable.XToolbar_hasSettings, false)
//            val hasConfirm = ta.getBoolean(R.styleable.XToolbar_hasConfirm, false)
//            val hasWebClose = ta.getBoolean(R.styleable.XToolbar_hasWebClose, false)
            val bgColor = ta.getColor(R.styleable.CoreToolbar_core_tbBackground, ResourceUtil.getColor(getContext(), android.R.color.white))

            setBackgroundColor(bgColor)

            if (hasStatusBar) {
                val statusBar = findViewById<View>(R.id.status_bar)
                val lp = statusBar.layoutParams
                lp.height = SystemUtil.getStatusBarHeight(context.resources)
                statusBar.layoutParams = lp
            }
            if (hasBackButton) {
                btn_back.visibility = View.VISIBLE
            }
//            if (hasWebClose) {
//                widget_toolbar_web_close.visibility = View.VISIBLE
//            }
//            if (hasCloseButton) {
//                back.visibility = View.VISIBLE
//                back.setImageResource(R.drawable.icon_close)
//            }
//            if (tabMode) {
//                title.visibility = View.GONE
//                back.visibility = View.GONE
//                tab.layoutResource = R.layout.widget_toolbar_tab
//                tabLayout = tab.inflate() as SmartTabLayout
//            }

            if (hasBottomLine) {
                bottom_line.visibility = View.VISIBLE
            } else {
                bottom_line.visibility = View.GONE
                if (hasElevation) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        elevation = resources.getDimension(R.dimen.core_toolbar_elevation)
                    }
                }
            }

//            if (isWhite) {
//                title.setTextColor(resources.getColor(android.R.color.white))
//                SystemUtils.setImageForegroundColor(back, context, android.R.color.white)
//            }
//
//            if (hasSettings) {
//                widget_settings.visibility = View.VISIBLE
//            }
//
//            if (hasConfirm) {
//                widget_toolbar_confirm.visibility = View.VISIBLE
//            }
        } finally {
            ta?.recycle()
        }
    }

    fun setTitleColor(@ColorRes color: Int) {
        title.setTextColor(resources.getColor(color))
    }

    fun setTitle(s: String?) {
        title.text = s
    }

    fun setTitle(@StringRes s: Int) {
        title.setText(s)
    }

    fun getTitleView() : TextView = title

    fun getBackButton() : ImageView = btn_back

//    fun settings() = widget_settings

    fun changeCategoryMenuStatus(isOpen: Boolean) {
//        menu_category.setImageResource(if (isOpen) R.drawable.icon_assets_close else R.drawable.icon_assets_open)
    }

    fun clearElevation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = 0f
        }
    }

    fun init(activity: Activity) : CoreToolbar {
        btn_back.setOnClickListener {
            activity.onBackPressed()
        }
        return this
    }

    fun initWithCallBack(activity: Activity, onback: () -> Unit = {}) : CoreToolbar {
        btn_back.setOnClickListener {
            onback()
            activity.onBackPressed()
        }
        return this
    }

//    fun setTitleToLeft() {
//        val lp = title.layoutParams as FrameLayout.LayoutParams
//        lp.gravity = Gravity.START or Gravity.CENTER_VERTICAL
//        lp.marginStart = resources.getDimensionPixelSize(R.dimen.home_title_start_margin)
//        title.layoutParams = lp
//    }

    fun customBackAction(listener: OnClickListener) {
        btn_back.setOnClickListener(listener)
    }

    fun setBackButtonColor(context: Context, @ColorRes color: Int) {
        SystemUtil.setImageForegroundColor(btn_back, context, color)
    }

//    fun openTextButton(name: String) : View {
//        text_button.visibility = View.VISIBLE
//        text_button.text = name
//        return text_button
//    }

//    fun confirmButton() = widget_toolbar_confirm
//
//    fun configWorkStatus(isWorking: Boolean) {
//        widget_work_status.visibility = View.VISIBLE
//        widget_work_status.switchWorkStatus(isWorking)
//    }
//
//    fun workStatus() = widget_work_status
//
//    fun configMessage(listener: (v: View) -> Unit) {
//        widget_toolbar_msg.visibility = View.VISIBLE
//        widget_toolbar_msg.setOnClickListener {
//            listener(it)
//        }
//    }

//    fun configRefresh(listener: (v: View) -> Unit) {
//        widget_toolbar_refresh.visibility = View.VISIBLE
//        widget_toolbar_refresh.setOnClickListener {
//            listener(it)
//        }
//    }
//
//    fun refreshButton() = widget_toolbar_refresh
//
//    fun configBillDetail(listener: (v: View) -> Unit) {
//        widget_bill_detail.visibility = View.VISIBLE
//        widget_bill_detail.setOnClickListener {
//            listener(it)
//        }
//    }
//
//    fun webCloseButton(): View? = widget_toolbar_web_close
//
//    fun configMoreOperation(listener: (v: View) -> Unit) {
//        widget_more_operation.visibility = View.VISIBLE
//        widget_more_operation.setOnClickListener {
//            listener(it)
//        }
//    }
}
