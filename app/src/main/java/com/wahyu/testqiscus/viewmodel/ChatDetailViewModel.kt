package com.wahyu.testqiscus.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahyu.testqiscus.TestQiscusRepository

class ChatDetailViewModel : ViewModel() {

    private var messageStatus = MutableLiveData<String>()
    private var repository: TestQiscusRepository = TestQiscusRepository()

    fun getStatusMessage(): LiveData<String> {
        return messageStatus
    }

    fun sendMessage(message: String, roomId: Long) {
        repository.sendMessage(message, roomId, object : OnMessageStatus {
            override fun OnStatusChange(status: String) {
                messageStatus.postValue(status)
            }
        })
    }

    interface OnMessageStatus {
        fun OnStatusChange(status: String)
    }
}