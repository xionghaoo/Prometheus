package xh.destiny.prometheus

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import xh.destiny.core.utils.CryptoUtil
import xh.destiny.core.utils.PhotoManager
import java.io.File

class MainActivity : AppCompatActivity() {

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

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageFile = File(photoManager.getPhotoPath(), pictureName)

            image_input_view.addImage(imageFile)
        }
    }
}
