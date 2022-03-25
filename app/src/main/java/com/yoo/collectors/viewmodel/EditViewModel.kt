package com.yoo.collectors.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yoo.collectors.Event
import com.yoo.collectors.model.EditRepository

class EditViewModel(private val repository: EditRepository): ViewModel() {

    private val _closeEvent = MutableLiveData<Event<Unit>>()
    val closeEvent: LiveData<Event<Unit>> = _closeEvent

    fun onBtnExitClick() {
        Log.d("click", "click")
        _closeEvent.value = Event(Unit)
    }

    fun test() {
        Log.d("click", "click")
    }

}