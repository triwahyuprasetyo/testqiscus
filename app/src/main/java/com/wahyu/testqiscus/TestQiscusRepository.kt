package com.wahyu.testqiscus

import com.qiscus.sdk.chat.core.QiscusCore
import com.qiscus.sdk.chat.core.data.model.QiscusAccount
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom
import com.qiscus.sdk.chat.core.data.model.QiscusComment
import com.qiscus.sdk.chat.core.data.remote.QiscusApi
import com.wahyu.testqiscus.model.ChatRoomResult
import com.wahyu.testqiscus.viewmodel.ChatDetailViewModel
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
        onStatusReady: ChatListViewModel.OnChatRoomListener
    ) {
        var chatRoomResult = ChatRoomResult(null, null)
        QiscusApi.getInstance().chatUser(email, null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { chatRoom: QiscusChatRoom? ->
                    if (chatRoom != null) {
                        chatRoomResult.chatRoom = chatRoom
                        chatRoomResult.status = ConstantVariable.SUCCESS
                        onStatusReady.OnChatRoomReady(chatRoomResult)
                    }
                }
            ) { throwable: Throwable? ->
                chatRoomResult.status = ConstantVariable.ERROR
                onStatusReady.OnChatRoomReady(chatRoomResult)
            }
    }


    fun sendMessage(
        message: String,
        roomId: Long,
        onMessageStatus: ChatDetailViewModel.OnMessageStatus
    ) {
        val qiscusMessage: QiscusComment = QiscusComment.generateMessage(roomId, message)
        QiscusApi.getInstance().sendMessage(qiscusMessage)
            .subscribeOn(Schedulers.io()) // need to run this task on IO thread
            .observeOn(AndroidSchedulers.mainThread()) // deliver result on main thread or UI thread
            .subscribe({ chatRoom: QiscusComment? ->
                onMessageStatus.OnStatusChange(ConstantVariable.SUCCESS)
            }
            ) { throwable: Throwable? ->
                onMessageStatus.OnStatusChange(ConstantVariable.ERROR)
            }
    }

}