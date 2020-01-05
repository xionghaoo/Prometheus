package com.ks.common.widgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import xh.destiny.core.R

class PlaceholderImageView : AppCompatImageView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        var ta: TypedArray? = null

        try {
            ta = context.theme.obtainStyledAttributes(attrs, R.styleable.PlaceholderImageView, 0, 0)
            val bg = ta.getResourceId(R.styleable.PlaceholderImageView_placeholder, R.drawable.widget_placeholder_default)
            background = resources.getDrawable(bg)
            scaleType = ScaleType.CENTER_CROP
        } finally {
            if (ta != null) {
                ta.recycle()
            }
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // setImage 会引起重新测量布局和绘制
        if (drawable != null) {
            background = null
        }
    }
}
