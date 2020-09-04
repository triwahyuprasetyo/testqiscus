package com.wahyu.testqiscus.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wahyu.testqiscus.TestQiscusRepository
import com.wahyu.testqiscus.model.ChatRoomResult

class ChatListViewModel : ViewModel() {
    private var chatRoomResult = MutableLiveData<ChatRoomResult>()
    private var repository: TestQiscusRepository = TestQiscusRepository()

    fun getChatRoom(): LiveData<ChatRoomResult> {
        return chatRoomResult
    }

    fun createChatRoom(email: String) {
        repository.getChatRoom(email, object : OnChatRoomListener {
            override fun OnChatRoomReady(chatRoom: ChatRoomResult) {
                chatRoomResult.value = chatRoom
            }
        })
    }

    interface OnChatRoomListener {
        fun OnChatRoomReady(chatRoomResult: ChatRoomResult)
    }
}