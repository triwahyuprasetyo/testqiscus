package com.wahyu.testqiscus.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom
import com.qiscus.sdk.chat.core.data.model.QiscusComment
import com.qiscus.sdk.chat.core.data.remote.QiscusApi
import com.qiscus.sdk.chat.core.data.remote.QiscusPusherApi
import com.qiscus.sdk.chat.core.event.QiscusChatRoomEvent
import com.qiscus.sdk.chat.core.event.QiscusCommentReceivedEvent
import com.wahyu.testqiscus.ConstantVariable
import com.wahyu.testqiscus.R
import com.wahyu.testqiscus.Utils
import kotlinx.android.synthetic.main.fragment_chat_detail.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ChatDetailFragment : Fragment() {

    private lateinit var fragmentActivity: FragmentActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentActivity = context as FragmentActivity
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onMessageReceived(event: QiscusCommentReceivedEvent) {
        println("message : " + event.qiscusComment.message + " - " + event.qiscusComment.sender) // to get the comment
        val context: Context = activity as Context
        Utils.showToast(
            context,
            event.qiscusComment.message + " - " + event.qiscusComment.senderEmail + " - " + event.qiscusComment.state
        )
//        QiscusCore.getDataStore().addOrUpdate(qiscusChatRoom)
        /*object : CountDownTimer(ConstantVariable.SPLASH_TIME, 1000) {
            override fun onTick(m: Long) {}
            override fun onFinish() {
                QiscusPusherApi.getInstance()
                    .markAsRead(event.qiscusComment.roomId, event.qiscusComment.id)
            }
        }.start()*/

    }

    @Subscribe
    fun onReceiveRoomEvent(roomEvent: QiscusChatRoomEvent) {
        when (roomEvent.event) {
            QiscusChatRoomEvent.Event.TYPING -> {
                roomEvent.roomId // this is the room id
                roomEvent.user // this is the qiscus user id
                roomEvent.isTyping // true if the user is typing

            }
            QiscusChatRoomEvent.Event.DELIVERED -> {
                roomEvent.roomId // this is the room id
                roomEvent.user // this is the qiscus user id
                roomEvent.commentId // the comment id was delivered
                println("DELIVEREDDDDDDD " + roomEvent.user)
            }
            QiscusChatRoomEvent.Event.READ -> {
                roomEvent.roomId // this is the room id
                roomEvent.user // this is the qiscus user id
                roomEvent.commentId // the comment id was read
                println("READDDDDDDDDDDD " + roomEvent.user)
            }
        }
    }

    var qiscusChatRoom: QiscusChatRoom? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_chat_detail, container, false)

        view.toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                fragmentActivity.supportFragmentManager.popBackStack()
            }
        })

        qiscusChatRoom = arguments?.get("qiscusChatRoom") as QiscusChatRoom?
        qiscusChatRoom?.let {
            view.toolbar.title = it.name
        }
        QiscusPusherApi.getInstance().subscribeChatRoom(qiscusChatRoom)

        val context: Context = activity as Context

        view.buttonSend.setOnClickListener {
            val text: String = view.editTextTextInput.text.toString().trim()
            if (text.isNotEmpty()) {
                qiscusChatRoom?.let {
                    sendMessage(text, qiscusChatRoom!!.id)
                }
            } else {
                Utils.showToast(context, getString(R.string.message_alert))
            }
        }

        return view
    }

    fun sendMessage(message: String, roomId: Long) {
        val qiscusMessage: QiscusComment = QiscusComment.generateMessage(roomId, message)
        QiscusApi.getInstance().sendMessage(qiscusMessage)
            .subscribeOn(Schedulers.io()) // need to run this task on IO thread
            .observeOn(AndroidSchedulers.mainThread()) // deliver result on main thread or UI thread
            .subscribe({ chatRoom: QiscusComment? ->
                println(ConstantVariable.SUCCESS)
            }
            ) { throwable: Throwable? ->
                println(ConstantVariable.ERROR)
            }
    }
}