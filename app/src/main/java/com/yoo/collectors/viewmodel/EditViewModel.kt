package com.yoo.collectors.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yoo.collectors.Event
import com.yoo.collectors.model.CropImage
import com.yoo.collectors.model.EditRepository

class EditViewModel(private val repository: EditRepository): ViewModel() {

    private val _closeEvent = MutableLiveData<Event<Unit>>()
    val closeEvent: LiveData<Event<Unit>> = _closeEvent

    var editedImageList = arrayListOf<CropImage>()
    var selected = 0

    fun onBtnExitClick() {
        _closeEvent.value = Event(Unit)
    }

    // TODO 여기 나중에 Room에서 가져오는걸로 교체
    fun initImageList(imgList: Array<ImageView>, patternList: Array<Int>) {
        for (i in imgList.indices) {
            editedImageList.add(CropImage(imgList[i], null, patternList[i]))
        }
    }

}