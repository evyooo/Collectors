package com.yoo.collectors.viewmodel

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    // TODO livedata로 교체필요
    var editedImageList = arrayListOf<CropImage>()
    var selected = 0

    fun onBtnExitClick() {
        _closeEvent.value = Event(Unit)
    }

    fun onShowOverlayClick(i: Int) {
        _showOverlayEvent.value = Event(Unit)
        selected = i
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
            editedImageList.add(CropImage(imgList[i], null, patternList[i]))
        }
    }

    fun changeCroppedImage(cropped: Bitmap?) {
        editedImageList[selected].croppedImg = cropped
    }

}