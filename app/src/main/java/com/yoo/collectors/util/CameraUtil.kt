package com.yoo.collectors.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yoo.collectors.R

const val CAMERA_CODE = 98
const val STORAGE_CODE = 99

private val CAMERA = arrayOf(Manifest.permission.CAMERA)
private val STORAGE = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

class CameraUtil(private val activity: Activity) {

    private fun checkPermission(permissions: Array<out String>, type: Int): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(activity, permissions, type)
                return false
            }
        }

        return true
    }

    fun requestPermissionResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(
                            activity,
                            R.string.toast_permission_camera,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            STORAGE_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(
                            activity,
                            R.string.toast_permission_gallery,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    fun callbackResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Bitmap? {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {
                    if (data?.extras?.get("data") != null) {
                        return data.extras?.get("data") as Bitmap
                    }
                }

                STORAGE_CODE -> {
                    return MediaStore.Images.Media.getBitmap(activity.contentResolver, data?.data)
                }
            }
        }
        return null
    }

    fun callCamera() {
        if (checkPermission(CAMERA, CAMERA_CODE) && checkPermission(STORAGE, STORAGE_CODE)) {
            val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            activity.startActivityForResult(itt, CAMERA_CODE)
        }
    }

    fun callGallery() {
        if (checkPermission(STORAGE, STORAGE_CODE)) {
            val itt = Intent(Intent.ACTION_PICK)
            itt.type = MediaStore.Images.Media.CONTENT_TYPE
            activity.startActivityForResult(itt, STORAGE_CODE)
        }
    }
}