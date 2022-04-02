package com.yoo.collectors

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yoo.collectors.model.CropImage
import jp.wasabeef.glide.transformations.MaskTransformation


// TODO onCreate시에만 불림.. 왠지 아직 모름
@BindingAdapter("selectedImage")
fun ImageView.bindSelectedImage(selectedImage: CropImage) {
    if (selectedImage.croppedImg == null) {
        Log.d("imageeditimageedit0", "null")
        Glide.with(this).load(R.color.white)
            .apply(RequestOptions.bitmapTransform(MaskTransformation(selectedImage.maskPattern)))
            .into(this)
    }
    else {
        Log.d("imageeditimageedit0", "${selectedImage.croppedImg}")
        Glide.with(this).load(selectedImage.croppedImg)
            .apply(RequestOptions.bitmapTransform(MaskTransformation(selectedImage.maskPattern)))
            .into(this)
    }
}
