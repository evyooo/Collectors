package com.yoo.collectors

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginRight
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yoo.collectors.databinding.ActivityImageEditBinding
import com.yoo.collectors.model.CropImage
import com.yoo.collectors.util.Crop
import com.yoo.collectors.viewmodel.EditViewModel
import jp.wasabeef.glide.transformations.MaskTransformation
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.FileOutputStream
import java.text.SimpleDateFormat


class ImageEditActivity : AppCompatActivity() {

    private val editViewModel: EditViewModel by viewModel()

    val CAMERA = arrayOf(Manifest.permission.CAMERA)
    val STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val CAMERA_CODE = 98
    val STORAGE_CODE = 99

    lateinit var imageList: Array<ImageView>
    lateinit var maskingPatternList: Array<Int>

    private lateinit var binding: ActivityImageEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = editViewModel
        setObserver()
        initImages()

    }

    private fun initImages() {
        imageList = arrayOf(
            binding.imageView, binding.imageView2, binding.imageView3, binding.imageView4
        )

        // TODO 이거 서버에서 받아오게 바꾸기
        maskingPatternList = arrayOf(
            R.drawable.crop1, R.drawable.crop2, R.drawable.crop3, R.drawable.crop4
        )
        if (editViewModel.editedImageList.isEmpty()) {
            editViewModel.initImageList(imageList, maskingPatternList)
        }
    }

    override fun onResume() {
        super.onResume()

        adjustSize()
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
        binding.imageView7.layoutParams.height = height.toInt()
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

    private fun setObserver() {
        // 뒤로가기
        editViewModel.closeEvent.observe(this) {
            onBackPressed()
        }

        // imageView 클릭시 overlay 보이기
        editViewModel.showOverlayEvent.observe(this) {
            selectDialog()
        }
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
        when (requestCode) {
            CAMERA_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "카메라 권한을 승인해 주세요.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            STORAGE_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "저장소 권한을 승인해 주세요.", Toast.LENGTH_LONG).show()
                        //finish() 앱을 종료함
                    }
                }
            }
        }
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


    // TODO 다이얼로그 -> 이미지뷰로 교체 예정
    fun selectDialog(){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_select, null)

        dialog.setContentView(view)
        dialog.show()

        val camera = view.findViewById<Button>(R.id.camera_btn_select)
        val gallery = view.findViewById<Button>(R.id.gallery_btn_select)

        camera.setOnClickListener {
            callCamera()
            dialog.dismiss()
            dialog.cancel()
        }

        gallery.setOnClickListener {
            getAlbum()
            dialog.dismiss()
            dialog.cancel()
        }
    }


    fun checkPermission(permissions: Array<out String>, type: Int): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, permissions, type)
                return false
            }
        }

        return true
    }

    private fun callCamera() {
        if (checkPermission(CAMERA, CAMERA_CODE) && checkPermission(STORAGE, STORAGE_CODE)) {
            val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(itt, CAMERA_CODE)
        }
    }

    fun getAlbum() {
        if (checkPermission(STORAGE, STORAGE_CODE)) {
            val itt = Intent(Intent.ACTION_PICK)
            itt.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(itt, STORAGE_CODE)
        }
    }

}