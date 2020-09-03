package com.wahyu.testqiscus.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahyu.testqiscus.TestQiscusRepository

class LoginViewModel : ViewModel() {

    private var status = MutableLiveData<String>()
    private var repository: TestQiscusRepository = TestQiscusRepository()

    fun getStatus(): LiveData<String> {
        return status
    }

    fun login(email: String, displayName: String) {
        repository.getStatusLogin(email, displayName, object : OnStatusReady {
            override fun OnStatus(st: String) {
                status.postValue(st)
            }
        })
    }

    interface OnStatusReady {
        fun OnStatus(status: String)
    }
}
