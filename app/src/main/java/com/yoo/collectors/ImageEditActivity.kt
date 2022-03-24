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
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yoo.collectors.databinding.ActivityImageEditBinding
import jp.wasabeef.glide.transformations.MaskTransformation
import java.io.FileOutputStream
import java.text.SimpleDateFormat


class ImageEditActivity : AppCompatActivity() {

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
    lateinit var cropList: Array<Int>

    private lateinit var binding: ActivityImageEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            imageList = arrayOf(
                imageView, imageView2, imageView3, imageView4
            )

            cropList = arrayOf(
                R.drawable.crop1, R.drawable.crop2, R.drawable.crop3, R.drawable.crop4
            )

            for (i in imageList.indices) {
                val img = imageList[i]
                img.setOnClickListener {
                    selectedImg = CropImage(img, cropList[i])
                    selectDialog()
                }
            }


            // ㅅㅏ이즈 맞추기 임시
            val size = Point()
            windowManager.defaultDisplay.getRealSize(size)
            val sizewidth = size.x
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val density = displayMetrics.density

            val height = (sizewidth - (40 * density)) * 5f / 3
            imageView7.layoutParams.height = convertPixelsToDp(height)
            imageList.forEach { img ->
                val calc = sizewidth * 32f / 125
                img.layoutParams.width = calc.toInt()
                img.layoutParams.height = calc.toInt()
            }

            setRightMargin(imageView, ((sizewidth - (40 * density)) * 16 / 1000).toInt())
            setTopMargin(imageView2, ((sizewidth - (40 * density)) * 36 / 1000).toInt())
            setRightMargin(imageView2, ((sizewidth - (40 * density)) * 45 / 1000).toInt())
            setTopMargin(imageView4, ((sizewidth - (40 * density)) * 24 / 1000).toInt())

        }

    }

    // TODO 클래스로 바꾸기
    // px을 dp로 변환 (px을 입력받아 dp를 리턴)
    private fun convertPixelsToDp(px: Float): Int {
        val resources: Resources = this.resources
        val metrics: DisplayMetrics = resources.displayMetrics
        return (px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
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

                        Glide.with(this).load(scaleCenterCrop(img, selectedImg.imageView.measuredWidth, selectedImg.imageView.measuredHeight))
                            .apply(RequestOptions.bitmapTransform(MaskTransformation(selectedImg.cropImg)))
                            .into(selectedImg.imageView)
                    }
                }

                STORAGE_CODE -> {
                    val uri = data?.data
//                    selected.setImageURI(uri)

                    // 여기서 크롭
                    val img = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    Glide.with(this).load(scaleCenterCrop(img, selectedImg.imageView.measuredWidth, selectedImg.imageView.measuredHeight))
                        .apply(RequestOptions.bitmapTransform(MaskTransformation(selectedImg.cropImg)))
                        .into(selectedImg.imageView)
                }
            }
        }
    }

    fun scaleCenterCrop(source: Bitmap, newHeight: Int, newWidth: Int): Bitmap? {
        val sourceWidth = source.width
        val sourceHeight = source.height

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        val xScale = newWidth.toFloat() / sourceWidth
        val yScale = newHeight.toFloat() / sourceHeight
        val scale = Math.max(xScale, yScale)

        // Now get the size of the source bitmap when scaled
        val scaledWidth = scale * sourceWidth
        val scaledHeight = scale * sourceHeight

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        val left = (newWidth - scaledWidth) / 2
        val top = (newHeight - scaledHeight) / 2

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        val dest = Bitmap.createBitmap(newWidth, newHeight, source.config)
        val canvas = Canvas(dest)
        canvas.drawBitmap(source, null, targetRect, null)
        return dest
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

        return uri;
    }

    data class CropImage(
        var imageView: ImageView,
        var cropImg: Int
    )
}