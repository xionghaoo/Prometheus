package xh.destiny.core.utils

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import com.ks.common.utils.SystemUtil

/**
 * Runtime权限申请
 */
class PermissionManager {

//    private var grantedCallback: () -> Unit = {}
//    private var _permissionName: String? = null
//
//    fun checkPermissions(permissions: Array<String>,
//                         permissionName: String?,
//                         granted: () -> Unit) {
//        _permissionName = permissionName
//        if (!hasAllPermissions(activity, permissions)) {
//            grantedCallback = granted
//
//            // Permission is not granted
//            // Should we show an explanation?
//            if (shouldShowAllRequestPermissionRationale(activity, permissions)) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//                Log.d(TAG, "PermissionManager: shouldShowRequestPermissionRationale")
//
//            } else {
//                // No explanation needed, we can request the permission.
//                requestPermissions(activity, permissions)
//                Log.d(TAG, "PermissionManager: requestPermissions")
//
//                // MY_PERMISSIONS_REQUEST_CAMERA is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        } else {
//            // Permission has already been granted
//            Log.d(TAG, "PermissionManager: Permission has already been granted")
//            granted()
//        }
//    }
//
//    private fun shouldShowAllRequestPermissionRationale(activity: Activity, permissions: Array<String>) : Boolean {
//        var shouldShowAllRationale = true
//        permissions.forEach { permission ->
//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
//                shouldShowAllRationale = false
//            }
//            Log.d(TAG, "PermissionManager: shouldShowAllRequestPermissionRationale: ${permission}, hasAllPermission: $shouldShowAllRationale")
//        }
//        return shouldShowAllRationale
//    }
//
//    private fun requestPermissions(activity: Activity, permissions: Array<String>) {
//        ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE)
//    }
//
//    fun onRequestPermissionsResult(requestCode: Int,
//                                   permissions: Array<String>,
//                                   grantResults: IntArray) {
//        Log.d(TAG, "PermissionManager: onRequestPermissionsResult: $requestCode")
//
//        when (requestCode) {
//            PERMISSION_REQUEST_CODE -> {
//                // If request is cancelled, the result arrays are empty.
//                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    Log.d(TAG, "PermissionManager: permission was granted, yay! Do the")
//
//                    grantedCallback()
//                } else {
//                    Log.d(TAG, "PermissionManager: permission denied, boo!")
//
//                    AlertDialog.Builder(activity)
//                        .setCancelable(false)
//                        .setMessage("尊敬的用户，您的${_permissionName.orEmpty()}权限被禁用了，请在系统设置中打开")
//                        .setPositiveButton("打开") { _, _ ->
//                            // 所有权限被永久限制，打开系统设置
//                            val intent = Intent()
//                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                            val uri = Uri.fromParts("package", activity.packageName, null)
//                            intent.data = uri
//                            activity.startActivity(intent)
//                        }
//                        .setNegativeButton("取消", null)
//                        .show()
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return
//            }
//
//            // Add other 'when' lines to check for other
//            // permissions this app might request.
//            else -> {
//                // Ignore all other requests.
//            }
//        }
//    }

    companion object {
        const val TAG = "PermissionManager"

//        const val PERMISSION_REQUEST_CODE = 110

        fun checkMulti(activity: Activity,
                       permissions: Array<String>,
                       isForceGrant: Boolean = true,
                       disallow: () -> Unit = {},
                       permissionName: String? = null,
                       allPermissionGranted: () -> Unit) {
//            Log.d("PermissionManager", "checkMulti")
            if (hasAllPermissions(activity, permissions)) {
                allPermissionGranted()
                return
            } else {
                if (isForceGrant) {
                    ToastUtil.show(activity, "您没有${permissionName ?: "相关"}权限")
                }
            }

            Dexter.withActivity(activity)
                .withPermissions(permissions.asList())
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
//                        Log.d("PermissionManager", "onPermissionsChecked: ${report?.areAllPermissionsGranted()}")
                        if (report?.areAllPermissionsGranted() == true) {
                            // 所有权限可用
                            allPermissionGranted()
                        } else {
                            disallow()
                        }
                        if (report?.isAnyPermissionPermanentlyDenied == true && isForceGrant) {
//                            CommonUtils.showToast(activity, "您的${permissionName ?: ""}权限被禁用了，请在系统设置中打开")
                            AlertDialog.Builder(activity)
                                .setCancelable(false)
                                .setMessage("尊敬的用户，您的${permissionName.orEmpty()}权限被禁用了，请在系统设置中打开")
                                .setPositiveButton("打开") { _, _ ->
                                    // 所有权限被永久限制，打开系统设置
                                    SystemUtil.openSettingsPermission(activity)
                                }
                                .setNegativeButton("取消", null)
                                .show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
//                        Log.d("PermissionManager", "onPermissionRationaleShouldBeShown: ${permissions}")

                        if (isForceGrant) {
                            // 有权限被禁止，提示用户授权
                            AlertDialog.Builder(activity)
                                .setCancelable(false)
                                .setMessage("尊敬的用户，为保证App的正常使用，需要您授予${permissionName ?: "相关"}权限。")
                                .setPositiveButton("授予权限") { _, _ ->
                                    token?.continuePermissionRequest()
                                }
                                .setNegativeButton("取消") { _, _ ->
                                    token?.cancelPermissionRequest()
                                }
                                .show()
                        } else {
                            token?.cancelPermissionRequest()
                        }
                    }
                })
                .check()
        }

        fun checkSingle(activity: Activity, permission: String,
                        permissionName: String? = null,
                        isForceGrant: Boolean = true,
                        permissionGrant: () -> Unit) {
            if (hasPermission(activity, permission)) {
                permissionGrant()
                return
            } else {
                if (isForceGrant) {
                    ToastUtil.show(activity, "您没有${permissionName ?: "相关"}权限")
                }
            }

            Dexter.withActivity(activity)
                .withPermission(permission)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
//                        Log.d("checkSingle", "onPermissionGranted")
                        permissionGrant()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
//                        Log.d("checkSingle", "onPermissionRationaleShouldBeShown")
                        if (isForceGrant) {
                            // 有权限被禁止，提示用户授权
                            AlertDialog.Builder(activity)
                                .setCancelable(false)
                                .setMessage("尊敬的用户，为保证App的正常使用，需要您授予${permissionName ?: "相关"}权限。")
                                .setPositiveButton("授予权限") { _, _ ->
                                    token?.continuePermissionRequest()
                                }
                                .setNegativeButton("取消") { _, _ ->
                                    token?.cancelPermissionRequest()
                                }
                                .show()
                        } else {
                            token?.cancelPermissionRequest()
                        }
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
//                        Log.d("checkSingle", "onPermissionDenied")
                        if (isForceGrant) {
                            ToastUtil.show(activity, "您的${permissionName.orEmpty()}权限被禁用了，请在系统设置中打开")
                            // 所有权限被永久限制，打开系统设置
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", activity.packageName, null)
                            intent.data = uri
                            activity.startActivity(intent)
                        }
                    }
                })
                .check()
        }

        private fun hasAllPermissions(activity: Activity, permissions: Array<String>) : Boolean {
            var hasAllPermissions = true
            permissions.forEach {  permission ->
                if (!hasPermission(activity, permission)) {
                    hasAllPermissions = false
                }
            }
            return hasAllPermissions
        }

        private fun hasPermission(activity: Activity, permission: String) : Boolean {
            return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}