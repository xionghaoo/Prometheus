package xh.destiny.core.widgets

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import xh.destiny.core.GlideApp
import xh.destiny.core.R
import java.io.File

class ImageInputView : LinearLayout {

    var maxImageNum = 3

    private var imageFrameSize: Int = 0
    lateinit var imageContainer: FlexboxLayout

    private var onAddImage: (() -> Unit)? = null

    private var currentAddViewIndex = 0

    private var imageCache = ArrayList<String>()

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setBackgroundColor(Color.WHITE)
        orientation = VERTICAL
        val padding = (resources.getDimension(R.dimen._15dp) / 2).toInt()
        val imageSize = resources.getDimension(R.dimen._80dp).toInt()
        val iconSize = resources.getDimension(R.dimen._16dp).toInt()
        val iconExtraSize = padding
        imageFrameSize = imageSize + iconExtraSize * 2

        // 初始化照片容器
        setPadding(padding, padding, padding, padding)
        imageContainer = FlexboxLayout(context)
        // 添加布局动画
        val anim = LayoutTransition()
        // 移除消失时的透明度动画
        anim.disableTransitionType(LayoutTransition.DISAPPEARING)
        imageContainer.layoutTransition = anim
        imageContainer.flexWrap = FlexWrap.WRAP
        addView(imageContainer)
        val lp = imageContainer.layoutParams
        lp.width = LayoutParams.MATCH_PARENT
        lp.height = LayoutParams.WRAP_CONTENT
        imageContainer.layoutParams = lp

        // 在容器中添加第一个新增图片的视图
        addNewSelectView()
    }

    private fun addNewSelectView() {
        val selectView = LayoutInflater.from(context).inflate(R.layout.widget_picture_upload_add, null)
        imageContainer.addView(selectView)
        val selectLp = selectView.layoutParams
        selectLp.width = imageFrameSize
        selectLp.height = imageFrameSize
        selectView.layoutParams = selectLp
        selectView.setOnClickListener {
            // 新增图片时记录此时新增视图的位置，便于删除后添加
            currentAddViewIndex = imageContainer.indexOfChild(selectView)
            onAddImage?.invoke()
        }
    }

    private fun deleteImage(removeView: View) {
        imageCache.removeAt(imageContainer.indexOfChild(removeView))
        imageContainer.removeView(removeView)

        // 删除一张图片以后，需要恢复新增视图
        var hasSelectView = false
        imageContainer.children.forEach { v ->
            if (v.id == R.id.widget_picture_upload_add) {
                hasSelectView = true
            }
        }
        if (!hasSelectView) {
            addNewSelectView()
        }
    }

    fun deleteAllImages() {
        imageContainer.removeAllViews()
        addNewSelectView()
        imageCache.clear()
    }

    fun getImages() = imageCache

    // 设置添加图片的方式，可以从相册选择或拍照
    fun setOnAddImage(call: () -> Unit) {
        onAddImage = call
    }

    fun recoverImageCache(images: ArrayList<String>?) {
        if (images == null) return

        // 自动添加图片时，需要跟踪和刷新新增视图的位置
        currentAddViewIndex = 0
        images.forEachIndexed { index, img ->
            val selectView = imageContainer.getChildAt(currentAddViewIndex)
            addImage(File(img))
            currentAddViewIndex = imageContainer.indexOfChild(selectView)
        }
    }

    fun addImage(img: File) {
        imageCache.add(img.toString())
        // 保存新增视图
        val selectView = imageContainer.getChildAt(currentAddViewIndex)
        imageContainer.removeViewAt(currentAddViewIndex)
        val imgView = LayoutInflater.from(context).inflate(R.layout.widget_picture_upload_image, null)
        imageContainer.addView(imgView)
        val lp = imgView.layoutParams
        lp.width = imageFrameSize
        lp.height = imageFrameSize
        imgView.layoutParams = lp
        imgView.findViewById<View>(R.id.upload_img_delete).setOnClickListener {
            deleteImage(it.parent as View)
        }

        GlideApp.with(this)
            .load(img)
            .centerCrop()
            .into(imgView.findViewById(R.id.widget_picture))

        if (imageContainer.childCount < maxImageNum) {
            imageContainer.addView(selectView)
            selectView.setOnClickListener {
                currentAddViewIndex = imageContainer.indexOfChild(selectView)
                onAddImage?.invoke()
            }
        }
    }

}