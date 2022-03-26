package com.yoo.collectors

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yoo.collectors.model.CropImage
import jp.wasabeef.glide.transformations.MaskTransformation


@BindingAdapter("selectedImage")
fun ImageView.bindSelectedImage(selectedImage: CropImage) {
    if (selectedImage.croppedImg == null) {
        Glide.with(this).load(R.color.white)
            .apply(RequestOptions.bitmapTransform(MaskTransformation(selectedImage.maskPattern)))
            .into(this)
    }
    else {
        Glide.with(this).load(selectedImage.croppedImg)
            .apply(RequestOptions.bitmapTransform(MaskTransformation(selectedImage.maskPattern)))
            .into(this)
    }
}
