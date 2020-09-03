package com.wahyu.testqiscus.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.qiscus.sdk.chat.core.QiscusCore
import com.qiscus.sdk.chat.core.data.model.QiscusAccount

class LoginViewModel : ViewModel() {

    private var status = MutableLiveData<String>()

    fun getStatus(): LiveData<String> {
        return status
    }

    fun login(email: String, displayName: String) {
        QiscusCore.setUser(email, "qiscuschat101")
            .withUsername(displayName)
            .withAvatarUrl("")
            .withExtras(null)
            .save(object : QiscusCore.SetUserListener {
                override fun onSuccess(success: QiscusAccount?) {
                    status.postValue("success")
                }

                override fun onError(error: Throwable?) {
                    status.postValue(error?.message.toString())
                }
            })
    }
}
