package xh.destiny.prometheus

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import xh.destiny.core.GlideApp
import xh.destiny.core.qrcode.ScannerActivity
import xh.destiny.core.utils.CryptoUtil
import xh.destiny.core.utils.PhotoManager
import xh.destiny.core.utils.ToastUtil
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE = 2
    }

    private lateinit var photoManager: PhotoManager

    private var pictureName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        photoManager = PhotoManager(this, "upload_images")


        image_input_view.setOnAddImage {
            pictureName = "${CryptoUtil.nameToMD5("test")}.jpg"

            photoManager.takePhoto(this, pictureName, 1)
        }

        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 2000
        valueAnimator.addUpdateListener { anim ->
            Log.d(TAG, "anim value: ${anim.animatedValue as Float}")
            btn_qrcode_scan.alpha = anim.animatedValue as Float
        }

        btn_qrcode_scan.setOnClickListener {
            ScannerActivity.start(this, REQUEST_CODE)
//            btn_qrcode_scan.alpha = 0f
//            valueAnimator.start()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageFile = File(photoManager.getPhotoPath(), pictureName)

            image_input_view.addImage(imageFile)
        }

        if (requestCode == REQUEST_CODE) {
            val result = IntentIntegrator.parseActivityResult(resultCode, data)
            ToastUtil.show(this, "${result.contents}")
        }
    }
}
