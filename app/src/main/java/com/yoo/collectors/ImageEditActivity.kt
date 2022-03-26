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

    // TODO 나중에 vm으로 옮기기
    private lateinit var selectedImg: CropImage

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
        editViewModel.setImageList()

        with(binding) {
            for (i in imageList.indices) {
                val img = imageList[i]
                img.setOnClickListener {
                    viewModel!!.selected = i
//                    selectedImg = CropImage(img, null, maskingPatternList[i])
                    selectDialog()
                }
            }
        }
    }

    private fun initImages() {
        imageList = arrayOf(
            binding.imageView, binding.imageView2, binding.imageView3, binding.imageView4
        )

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
//                        val uri = saveFile(RandomFileName(), "image/jpg", img)
//                        selected.setImageBitmap(img)

                        val selectedImage = editViewModel.editedImageList[editViewModel.selected]
                        editViewModel.changeCroppedImage(Crop().scaleCenterCrop(img, selectedImage.imageView.measuredWidth, selectedImage.imageView.measuredHeight))
//                        editViewModel.changeCroppedImage(Crop().scaleCenterCrop(img, selectedImg.imageView.measuredWidth, selectedImg.imageView.measuredHeight))
//                        selectedImg.croppedImg = Crop().scaleCenterCrop(img, selectedImg.imageView.measuredWidth, selectedImg.imageView.measuredHeight)
                        Glide.with(this).load(selectedImage.croppedImg)
                            .apply(RequestOptions.bitmapTransform(MaskTransformation(selectedImage.maskPattern)))
                            .into(selectedImage.imageView)
//                        Glide.with(this).load(selectedImg.croppedImg)
//                            .apply(RequestOptions.bitmapTransform(MaskTransformation(selectedImg.maskPattern!!)))
//                            .into(selectedImg.imageView)
                    }
                }

                STORAGE_CODE -> {
                    val uri = data?.data
//                    selected.setImageURI(uri)

                    // 여기서 크롭
                    val img = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    val selectedImage = editViewModel.editedImageList[editViewModel.selected]
                    editViewModel.changeCroppedImage(Crop().scaleCenterCrop(img, selectedImage.imageView.measuredWidth, selectedImage.imageView.measuredHeight))
//                    editViewModel.changeCroppedImage(Crop().scaleCenterCrop(img, selectedImg.imageView.measuredWidth, selectedImg.imageView.measuredHeight))
//                    selectedImg.croppedImg = Crop().scaleCenterCrop(img, selectedImg.imageView.measuredWidth, selectedImg.imageView.measuredHeight)
                    Glide.with(this).load(selectedImage.croppedImg)
                        .apply(RequestOptions.bitmapTransform(MaskTransformation(selectedImage.maskPattern)))
                        .into(selectedImage.imageView)
//                    Glide.with(this).load(selectedImg.croppedImg)
//                        .apply(RequestOptions.bitmapTransform(MaskTransformation(selectedImg.maskPattern!!)))
//                        .into(selectedImg.imageView)
                }
            }
        }
    }



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
//        val intent = Intent(this, SelectActivity::class.java)
//        startActivity(intent)
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

    fun RandomFileName() : String
    {
        val fineName = SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
        return fineName
    }

    fun saveFile(fileName: String, mimeType: String, bitmap: Bitmap): Uri? {
        var CV = ContentValues()
        CV.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        CV.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            CV.put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CV)

        if (uri != null) {
            var scriptor = contentResolver.openFileDescriptor(uri, "w")

            if (scriptor != null) {
                val fos = FileOutputStream(scriptor.fileDescriptor)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    CV.clear()
                    CV.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(uri, CV, null, null)
                }
            }
        }

        return uri
    }
}