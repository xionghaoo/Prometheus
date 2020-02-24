package xh.destiny.core.widgets

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import xh.destiny.core.R

/**
 * 圆角视图，用于覆盖相机
 */
class CornerView : View {

    companion object {
        private var WRAP_WIDTH = 60
        private var WRAP_HEIGHT = 60
        private const val SWEEP_ANGLE = 90f

    }

    enum class Direction {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    private var direction: Direction? = null
    private lateinit var path: Path
    private lateinit var paint: Paint

    private var rect: RectF? = null

    constructor(context: Context) : super(context) {
//        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        var ta: TypedArray? = null
        try {
            ta = context.theme.obtainStyledAttributes(attrs, R.styleable.CornerView, 0, 0)
            val color = ta.getColor(R.styleable.CornerView_cv_color, resources.getColor(R.color.white))
            direction = Direction.values()[ta.getInt(R.styleable.CornerView_cv_direction, 0)]
            init(color)
        } finally {
            ta?.recycle()
        }
    }

    fun init(color: Int) {
        path = Path()

        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        paint.style = Paint.Style.FILL_AND_STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(WRAP_WIDTH, WRAP_HEIGHT)
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(WRAP_WIDTH, heightSize)
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, WRAP_HEIGHT)
        } else {
            setMeasuredDimension(widthSize, heightSize)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val topPadding = paddingTop
        val bottomPadding = paddingBottom
        val leftPadding = paddingLeft
        val rightPadding = paddingRight
        val width = (width - leftPadding - rightPadding).toFloat()
        val height = (height - topPadding - bottomPadding).toFloat()

        if (rect == null) {
            when (direction) {
                Direction.LEFT_TOP -> {
                    rect = RectF(0f, 0f, width * 2, height * 2)
                }
                Direction.LEFT_BOTTOM -> {
                    rect = RectF(0f, 0f - height, width * 2, height)
                }
                Direction.RIGHT_TOP -> {
                    rect = RectF(0f - width, 0f, width, height * 2)
                }
                Direction.RIGHT_BOTTOM -> {
                    rect = RectF(0f - width, 0f - height, width, height)
                }
            }
        }
        path.reset()
        when (direction) {
            Direction.LEFT_TOP -> {
                path.arcTo(rect, 180f, SWEEP_ANGLE)
                path.lineTo(0f, 0f)
                path.lineTo(0f, height)
            }
            Direction.LEFT_BOTTOM -> {
                path.lineTo(0f, height)
                path.lineTo(width, height)
                path.arcTo(rect, 90f, SWEEP_ANGLE)
            }
            Direction.RIGHT_TOP -> {
                path.arcTo(rect, 270f, SWEEP_ANGLE)
                path.lineTo(width, 0f)
                path.lineTo(0f, 0f)
            }
            Direction.RIGHT_BOTTOM -> {
                path.moveTo(width, 0f)
                path.arcTo(rect, 0f, SWEEP_ANGLE)
                path.lineTo(width, height)
                path.lineTo(width, 0f)
            }
        }
        path.close()
        canvas?.drawPath(path, paint)
    }

}
