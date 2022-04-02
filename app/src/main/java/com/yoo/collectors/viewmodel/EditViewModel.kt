package com.yoo.collectors.viewmodel

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.yoo.collectors.Event
import com.yoo.collectors.R
import com.yoo.collectors.SingleLiveData
import com.yoo.collectors.model.CropImage
import com.yoo.collectors.model.EditRepository

class EditViewModel(private val repository: EditRepository): ViewModel() {

    private val _closeEvent = MutableLiveData<Event<Unit>>()
    val closeEvent: LiveData<Event<Unit>> = _closeEvent

    private val _showOverlayEvent = SingleLiveData<Event<Unit>>()
    val showOverlayEvent: LiveData<Event<Unit>> = _showOverlayEvent

    private val _hideOverlayEvent = SingleLiveData<Event<Unit>>()
    val hideOverlayEvent: LiveData<Event<Unit>> = _hideOverlayEvent

    private val _selectCameraEvent = SingleLiveData<Event<Unit>>()
    val selectCameraEvent: LiveData<Event<Unit>> = _selectCameraEvent

    private val _selectGalleryEvent = SingleLiveData<Event<Unit>>()
    val selectGalleryEvent: LiveData<Event<Unit>> = _selectGalleryEvent

    private val _test = SingleLiveData<String>()
    val test: LiveData<String> = _test

    // TODO livedata로 교체필요
    var editedImageList = arrayListOf<CropImage>()
    var selected = 0

    val editImageList = arrayListOf<SingleLiveData<CropImage>>()

    fun settest() {
        _test.value = "changed"
    }

    fun onBtnExitClick() {
        _closeEvent.value = Event(Unit)
    }

    fun onShowOverlayClick(i: Int) {
        selected = i
        if (editImageList[selected].value!!.croppedImg == null) {
            _showOverlayEvent.value = Event(Unit)
        }
        else {
            // TODO 여기 편집
        }
    }

    fun onHideOverlayClick() {
        _hideOverlayEvent.value = Event(Unit)
    }

    fun onSelectClick(isCamera: Boolean) {
        if (isCamera) {
            _selectCameraEvent.value = Event(Unit)
        }
        else {
            _selectGalleryEvent.value = Event(Unit)
        }
        _hideOverlayEvent.value = Event(Unit)
    }

    // TODO 여기 나중에 Room에서 가져오는걸로 교체
    // pattern은 서버에서 받아올듯 background도..
    fun initImageList(imgList: Array<ImageView>) {
        val patternList = arrayOf(
            R.drawable.crop1, R.drawable.crop2, R.drawable.crop3, R.drawable.crop4
        )
        for (i in imgList.indices) {
//            editedImageList.add(CropImage(imgList[i], null, patternList[i]))

            val cropImage = SingleLiveData<CropImage>()
            cropImage.value = CropImage(imgList[i], null, patternList[i])
            editImageList.add(cropImage)
        }
    }

    fun changeCroppedImage(cropped: Bitmap?) {
//        editedImageList[selected].croppedImg = cropped
//        editImageList[selected].value?.croppedImg = cropped
        val cropImage = editImageList[selected].value!!
        Log.d("imageeditimageedit4a", "${editImageList[0].value!!.croppedImg}")
        editImageList[selected].postValue(CropImage(cropImage.imageView, cropped, cropImage.maskPattern))
        Log.d("imageeditimageedit4b", "${editImageList[0].value!!.croppedImg}")
        editImageList[selected].value?.croppedImg = cropped
        Log.d("imageeditimageedit4c", "${editImageList[0].value!!.croppedImg}")

    }
}