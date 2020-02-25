package xh.destiny.core.qrcode

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.android.synthetic.main.activity_scanner.*
import kotlinx.android.synthetic.main.custom_barcode_scanner.*
import xh.destiny.core.R

class ScannerActivity : AppCompatActivity() {

    companion object {
        fun start(activity: Activity, requestCode: Int) {
            IntentIntegrator(activity)
                .setOrientationLocked(true)
                .setRequestCode(requestCode)
                .setCaptureActivity(ScannerActivity::class.java)
                .initiateScan()
        }
    }

    private lateinit var capture: CaptureManager
    private var isTorchOpen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        zxing_barcode_scanner.setTorchListener(object : DecoratedBarcodeView.TorchListener {
            override fun onTorchOn() {
                btn_open_torch.setImageResource(R.drawable.ic_torch_open)
            }

            override fun onTorchOff() {
                btn_open_torch.setImageResource(R.drawable.ic_torch_close)
            }
        })
        capture = CaptureManager(this, zxing_barcode_scanner)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.decode()

        zxing_viewfinder_view.setLaserVisibility(true)

        btn_open_torch.setOnClickListener {
            isTorchOpen = !isTorchOpen
            openTorch()
        }
    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return zxing_barcode_scanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    private fun openTorch() {
        if (isTorchOpen) {
            zxing_barcode_scanner.setTorchOn()
        } else {
            zxing_barcode_scanner.setTorchOff()
        }
    }

}
