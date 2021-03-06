package com.wahyu.testqiscus.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.qiscus.sdk.chat.core.QiscusCore
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom
import com.qiscus.sdk.chat.core.data.model.QiscusComment
import com.qiscus.sdk.chat.core.data.remote.QiscusPusherApi
import com.qiscus.sdk.chat.core.event.QiscusChatRoomEvent
import com.qiscus.sdk.chat.core.event.QiscusCommentReceivedEvent
import com.wahyu.testqiscus.ConstantVariable
import com.wahyu.testqiscus.R
import com.wahyu.testqiscus.Utils
import com.wahyu.testqiscus.ui.adapter.MessageAdapter
import com.wahyu.testqiscus.viewmodel.ChatDetailViewModel
import kotlinx.android.synthetic.main.fragment_chat_detail.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ChatDetailFragment : Fragment() {

    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapterRecyclerView: MessageAdapter
    private lateinit var qiscusCommentList: MutableList<QiscusComment>
    private val model: ChatDetailViewModel by viewModels()

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
        showAndManageLastComment(event.qiscusComment)

        qiscusChatRoom?.lastComment = event.qiscusComment
        QiscusCore.getDataStore().addOrUpdate(qiscusChatRoom)
    }

    fun showAndManageLastComment(qiscusComment: QiscusComment) {
        qiscusCommentList.add(qiscusComment)
        adapterRecyclerView.notifyDataSetChanged()

        //delay for testing
        object : CountDownTimer(ConstantVariable.SPLASH_TIME, 2000) {

            override fun onTick(m: Long) {}
            override fun onFinish() {
                QiscusPusherApi.getInstance()
                    .markAsRead(qiscusComment.roomId, qiscusComment.id)
            }
        }.start()
    }

    @Subscribe
    fun onReceiveRoomEvent(roomEvent: QiscusChatRoomEvent) {
        when (roomEvent.event) {
            QiscusChatRoomEvent.Event.TYPING -> {
                qiscusChatRoom?.let {
                    if (it.id == roomEvent.roomId) {
                        if (roomEvent.isTyping) toolbar.subtitle =
                            getString(R.string.is_typing).toString()
                        else toolbar.subtitle = ""
                    }
                }
            }
            QiscusChatRoomEvent.Event.DELIVERED -> {
                sendDelivered(roomEvent.commentId)
            }
            QiscusChatRoomEvent.Event.READ -> {
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
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_chat_detail, container, false)

        toolbar = view.toolbar
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                fragmentActivity.supportFragmentManager.popBackStack()
            }
        })

        qiscusChatRoom = arguments?.get("qiscusChatRoom") as QiscusChatRoom?
        qiscusChatRoom?.let {
            toolbar.title = it.name
        }
        QiscusPusherApi.getInstance().subscribeChatRoom(qiscusChatRoom)

        val context: Context = activity as Context

        model.getStatusMessage().observe(viewLifecycleOwner, Observer<String> {
            if (it.equals(ConstantVariable.SUCCESS)) {
                Utils.showToast(context, context.getString(R.string.message_send))
            } else {
                Utils.showToast(context, context.getString(R.string.unable_send_message))
            }
        })

        view.buttonSend.setOnClickListener {
            val text: String = view.editTextTextInput.text.toString().trim()
            if (text.isNotEmpty()) {
                qiscusChatRoom?.let {
                    model.sendMessage(text, qiscusChatRoom!!.id)
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
            if (it.lastComment != null) {
                showAndManageLastComment(it.lastComment)
            }


            view.editTextTextInput.onFocusChangeListener = object : View.OnFocusChangeListener {
                override fun onFocusChange(v: View?, hasFocus: Boolean) {
                    if (hasFocus) {
                        //click edittext
                        QiscusPusherApi.getInstance().publishTyping(qiscusChatRoom!!.id, true)
                    } else {
                        //close detail chat
                        QiscusPusherApi.getInstance().publishTyping(qiscusChatRoom!!.id, false)
                    }
                }
            }

        }

        return view
    }

}