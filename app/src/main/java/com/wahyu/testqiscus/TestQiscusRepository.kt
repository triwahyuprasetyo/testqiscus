package com.wahyu.testqiscus

import com.qiscus.sdk.chat.core.QiscusCore
import com.qiscus.sdk.chat.core.data.model.QiscusAccount
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom
import com.qiscus.sdk.chat.core.data.remote.QiscusApi
import com.wahyu.testqiscus.viewmodel.ChatListViewModel
import com.wahyu.testqiscus.viewmodel.LoginViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

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

    fun getChatRoom(
        email: String,
        onStatusReady: ChatListViewModel.OnStatusReady
    ) {
        QiscusApi.getInstance().chatUser(email, null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { chatRoom: QiscusChatRoom? ->
                    onStatusReady.OnStatus(chatRoom?.id.toString())
                }
            ) { throwable: Throwable? ->
                onStatusReady.OnStatus(ConstantVariable.ERROR)
            }

    }


}