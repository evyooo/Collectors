package com.yoo.collectors

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.yoo.collectors.databinding.ActivityImageEditBinding
import com.yoo.collectors.util.CameraUtil
import com.yoo.collectors.util.Crop
import com.yoo.collectors.viewmodel.EditViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ImageEditActivity : AppCompatActivity() {

    val CAMERA_CODE = 98
    val STORAGE_CODE = 99

    lateinit var imageList: Array<ImageView>
    lateinit var cameraUtil: CameraUtil

    private val editViewModel: EditViewModel by viewModel()
    private lateinit var binding: ActivityImageEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = editViewModel
        setObserver()
        initImages()
        adjustSize()

        cameraUtil = CameraUtil(this)

    }

    private fun setObserver() {
        // 뒤로가기
        editViewModel.closeEvent.observe(this) {
            onBackPressed()
        }

        // imageView 클릭시 overlay 보이기
        editViewModel.showOverlayEvent.observe(this) {
            binding.overlayClEdit.visibility = View.VISIBLE
        }

        editViewModel.hideOverlayEvent.observe(this) {
            binding.overlayClEdit.visibility = View.GONE
        }

        editViewModel.selectCameraEvent.observe(this) {
            cameraUtil.callCamera()
        }

        editViewModel.selectGalleryEvent.observe(this) {
            cameraUtil.callGallery()
        }
    }

    private fun initImages() {
        imageList = arrayOf(
            binding.imageView, binding.imageView2, binding.imageView3, binding.imageView4
        )

        if (editViewModel.editedImageList.isEmpty()) {
            editViewModel.initImageList(imageList)
        }
    }

    private fun adjustSize() {
        // ㅅㅏ이즈 맞추기 임시
        val size = Point()
        windowManager.defaultDisplay.getRealSize(size)
        val sizewidth = size.x
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val density = displayMetrics.density

//            val width = (sizewidth - (40 * density))
        val width = sizewidth

        val height = width * 3f / 5
        binding.backgroundIvEdit.layoutParams.height = height.toInt()
        imageList.forEach { img ->
            val calc = width * 32f / 125
            img.layoutParams.width = calc.toInt()
            img.layoutParams.height = calc.toInt()
        }

        Log.d("margin", "$width ${width * 16f / 1000} ${(width * 16f / 1000).toInt()} ")
        Log.d("margin", "$width ${width * 36f / 1000} ${(width * 36f / 1000).toInt()} ")
        Log.d("margin", "$width ${width * 45f / 1000} ${(width * 45f / 1000).toInt()} ")
        Log.d("margin", "$width ${width * 24f / 1000} ${(width * 24f / 1000).toInt()} ")

        setRightMargin(binding.imageView, (width * 16f / 1000).toInt())
        setTopMargin(binding.imageView2, (width * 36f / 1000).toInt())
        setRightMargin(binding.imageView2, (width * 45f / 1000).toInt())
        setTopMargin(binding.imageView4, (width * 24f / 1000).toInt())
    }



    private fun setRightMargin(imageView: ImageView, int: Int) {
        val layoutParams: ConstraintLayout.LayoutParams = imageView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.marginEnd = int
    }

    private fun setTopMargin(imageView: ImageView, int: Int) {
        val layoutParams: ConstraintLayout.LayoutParams = imageView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topMargin = int
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraUtil.requestPermissionResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {
                    if (data?.extras?.get("data") != null) {
                        val img = data.extras?.get("data") as Bitmap
                        val selectedImage = editViewModel.editedImageList[editViewModel.selected]
                        editViewModel.changeCroppedImage(Crop().scaleCenterCrop(img, selectedImage.imageView.measuredWidth, selectedImage.imageView.measuredHeight))
                    }
                }

                STORAGE_CODE -> {
                    val img = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data)
                    val selectedImage = editViewModel.editedImageList[editViewModel.selected]
                    editViewModel.changeCroppedImage(Crop().scaleCenterCrop(img, selectedImage.imageView.measuredWidth, selectedImage.imageView.measuredHeight))
                }
            }
        }
    }
}