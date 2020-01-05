package xh.destiny.core.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.lang.Exception
import java.lang.IllegalArgumentException
import android.hardware.camera2.CameraCharacteristics
import androidx.core.content.ContextCompat.getSystemService
import android.hardware.camera2.CameraManager
import android.util.Log


/**
 * 拍照管理
 * 调起系统相机拍照
 * 默认照片存放位置: 外部存储卡缓存路径
 * fold: 存放图片的文件夹
 */
class PhotoManager(context: Context, fold: String? = null) {

    private val pictureDir: File

    init {
        val subPath = fold ?: "default"

        val cachePath = File(context.externalCacheDir, subPath)
        if (!cachePath.exists()) {
            cachePath.mkdir()
        }

        pictureDir = cachePath
    }

    /**
     * 调起系统相机拍照
     * pic: 图片名称，如avatar.jpg
     * 回调onActivityResult的result code为Activity.RESULT_OK
     */
    fun takePhoto(context: Activity, pictureName: String, requestCode: Int) {
        PermissionManager.checkMulti(context,
            arrayOf(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE),
            permissionName = "相机"
        ) {
            if (pictureName.indexOf(".jpg") <= 0
                && pictureName.indexOf(".png") <= 0) {
                throw IllegalArgumentException("照片名必须以.jpg或.png结尾")
            }
            val file = File(pictureDir, pictureName)
            try {
                file.createNewFile()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val outputUri: Uri?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                outputUri = FileProvider.getUriForFile(context, "com.ks.lion.provider", file)
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                outputUri = Uri.fromFile(file)
            }

            i.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)

            context.startActivityForResult(i, requestCode)
        }


    }

    /**
     * 打开相册，选择照片
     * 回调onActivityResult的result code为Activity.RESULT_OK
     */
    fun selectPhoto(context: Activity, requestCode: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        context.startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)
    }

    fun getPhotoPath() = pictureDir

}