package com.wahyu.testqiscus.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.qiscus.sdk.chat.core.QiscusCore
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom
import com.qiscus.sdk.chat.core.data.model.QiscusComment
import com.qiscus.sdk.chat.core.data.remote.QiscusApi
import com.qiscus.sdk.chat.core.data.remote.QiscusPusherApi
import com.qiscus.sdk.chat.core.event.QiscusChatRoomEvent
import com.qiscus.sdk.chat.core.event.QiscusCommentReceivedEvent
import com.wahyu.testqiscus.ConstantVariable
import com.wahyu.testqiscus.R
import com.wahyu.testqiscus.Utils
import com.wahyu.testqiscus.ui.adapter.MessageAdapter
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

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapterRecyclerView: MessageAdapter
    private lateinit var qiscusCommentList: MutableList<QiscusComment>

    @Subscribe
    fun onMessageReceived(event: QiscusCommentReceivedEvent) {
        qiscusCommentList.add(event.qiscusComment)
        adapterRecyclerView.notifyDataSetChanged()
        object : CountDownTimer(ConstantVariable.SPLASH_TIME, 2000) {//delay for testing
            override fun onTick(m: Long) {}
            override fun onFinish() {
                QiscusPusherApi.getInstance()
                    .markAsRead(event.qiscusComment.roomId, event.qiscusComment.id)
            }
        }.start()

        qiscusChatRoom?.lastComment=event.qiscusComment
        QiscusCore.getDataStore().addOrUpdate(qiscusChatRoom)
    }

    @Subscribe
    fun onReceiveRoomEvent(roomEvent: QiscusChatRoomEvent) {
        when (roomEvent.event) {
            QiscusChatRoomEvent.Event.DELIVERED -> {
                roomEvent.roomId // this is the room id
                roomEvent.user // this is the qiscus user id
                roomEvent.commentId // the comment id was delivered
                sendDelivered(roomEvent.commentId)
            }
            QiscusChatRoomEvent.Event.READ -> {
                roomEvent.roomId // this is the room id
                roomEvent.user // this is the qiscus user id
                roomEvent.commentId // the comment id was read
                sendReceive(roomEvent.commentId)
            }
        }
    }

    fun sendDelivered(commentId: Long) {
        for (i in 0 until qiscusCommentList.size) {
            if (qiscusCommentList.get(i).id == commentId) {
                qiscusCommentList.get(i).state = 3
                break
            }
        }
        adapterRecyclerView.notifyDataSetChanged()
    }

    fun sendReceive(commentId: Long) {
        for (i in 0 until qiscusCommentList.size) {
            if (qiscusCommentList.get(i).id == commentId) {
                qiscusCommentList.get(i).state = 4
                break
            }
        }
        adapterRecyclerView.notifyDataSetChanged()
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
                    view.editTextTextInput.text.clear()
                }
            } else {
                Utils.showToast(context, getString(R.string.message_alert))
            }
        }

        qiscusCommentList = mutableListOf<QiscusComment>()
        linearLayoutManager = LinearLayoutManager(context)
        view.recyclerviewDetail.layoutManager = linearLayoutManager
        adapterRecyclerView =
            MessageAdapter(
                listData = qiscusCommentList,
                context = context
            )
        view.recyclerviewDetail.adapter = adapterRecyclerView

        qiscusChatRoom?.let {
            if (it.lastComment!=null){
                qiscusCommentList.add(it.lastComment)
                adapterRecyclerView.notifyDataSetChanged()
                object : CountDownTimer(ConstantVariable.SPLASH_TIME, 2000) {//delay for testing
                override fun onTick(m: Long) {}
                    override fun onFinish() {
                        QiscusPusherApi.getInstance()
                            .markAsRead(it.lastComment.roomId, it.lastComment.id)
                    }
                }.start()
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