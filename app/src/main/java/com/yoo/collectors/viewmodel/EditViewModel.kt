package com.yoo.collectors.viewmodel

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yoo.collectors.Event
import com.yoo.collectors.SingleLiveData
import com.yoo.collectors.model.CropImage
import com.yoo.collectors.model.EditRepository

class EditViewModel(private val repository: EditRepository): ViewModel() {

    private val _closeEvent = MutableLiveData<Event<Unit>>()
    val closeEvent: LiveData<Event<Unit>> = _closeEvent

    private val _showOverlayEvent = SingleLiveData<Event<Unit>>()
    val showOverlayEvent: LiveData<Event<Unit>> = _showOverlayEvent

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

    // TODO 여기 나중에 Room에서 가져오는걸로 교체
    // pattern은 서버에서 받아올듯
    fun initImageList(imgList: Array<ImageView>, patternList: Array<Int>) {
        for (i in imgList.indices) {
            editedImageList.add(CropImage(imgList[i], null, patternList[i]))
        }
    }

    fun changeCroppedImage(cropped: Bitmap?) {
        editedImageList[selected].croppedImg = cropped
    }

}