package com.yoo.collectors

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yoo.collectors.model.CropImage
import jp.wasabeef.glide.transformations.MaskTransformation


@BindingAdapter("selectedImage")
fun ImageView.bindSelectedImage(selectedImage: CropImage) {
    selectedImage.let {
        Glide.with(this).load(selectedImage.croppedImg)
            .apply(RequestOptions.bitmapTransform(MaskTransformation(selectedImage.maskPattern)))
            .into(this)
    }
}