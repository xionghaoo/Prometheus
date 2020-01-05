package xh.destiny.core.utils

import android.app.Activity
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log

class CameraUtil {
    companion object {
        fun checkCamera2Support(activity: Activity) {
            val manager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager

            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)


                Log.d(
                    "CameraUtil",
                    "INFO_SUPPORTED_HARDWARE_LEVEL " + characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)!!
                )
            }
        }

        // Returns true if the device supports the required hardware level, or better.
        fun isHardwareLevelSupported(c: CameraCharacteristics, requiredLevel: Int): Boolean {
            val sortedHwLevels = intArrayOf(
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3
            )
            val deviceLevel = c.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)!!
            if (requiredLevel == deviceLevel) {
                return true
            }

            for (sortedlevel in sortedHwLevels) {
                if (sortedlevel == requiredLevel) {
                    return true
                } else if (sortedlevel == deviceLevel) {
                    return false
                }
            }
            return false // Should never reach here
        }
    }
}