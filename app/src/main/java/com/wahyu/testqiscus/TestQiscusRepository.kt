package com.wahyu.testqiscus

import com.qiscus.sdk.chat.core.QiscusCore
import com.qiscus.sdk.chat.core.data.model.QiscusAccount
import com.wahyu.testqiscus.viewmodel.LoginViewModel

class TestQiscusRepository {

    fun getStatusLogin(
        email: String,
        displayName: String,
        onStatusReady: LoginViewModel.OnStatusReady
    ) {
        QiscusCore.setUser(email, ConstantVariable.USERKEY)
            .withUsername(displayName)
            .withAvatarUrl("")
            .withExtras(null)
            .save(object : QiscusCore.SetUserListener {
                override fun onSuccess(success: QiscusAccount?) {
                    onStatusReady.OnStatus(ConstantVariable.SUCCESS)

                }

                override fun onError(error: Throwable?) {
                    onStatusReady.OnStatus(error?.message.toString())
                }
            })
    }
}