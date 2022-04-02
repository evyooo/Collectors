package com.yoo.collectors.model

import android.graphics.Bitmap
import android.widget.ImageView

data class CropImage(
    val imageView: ImageView,
    var croppedImg: Bitmap?,
    var maskPattern: Int
)