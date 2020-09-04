package com.wahyu.testqiscus.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.qiscus.sdk.chat.core.QiscusCore
import com.qiscus.sdk.chat.core.data.model.QiscusComment
import com.qiscus.sdk.chat.core.data.remote.QiscusApi
import com.qiscus.sdk.chat.core.data.remote.QiscusPusherApi
import com.qiscus.sdk.chat.core.event.QiscusCommentReceivedEvent
import com.wahyu.testqiscus.ConstantVariable
import com.wahyu.testqiscus.R
import com.wahyu.testqiscus.Utils
import com.wahyu.testqiscus.model.ChatRoomResult
import com.wahyu.testqiscus.viewmodel.ChatListViewModel
import kotlinx.android.synthetic.main.dialog_new_contact.view.*
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.fragment_chat_list.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class ChatListFragment : Fragment() {

    private lateinit var fragmentActivity: FragmentActivity
    private val model: ChatListViewModel by viewModels()

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
        println("message : " + event.qiscusComment.message) // to get the comment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_chat_list, container, false)
        view.button.setOnClickListener {
            /*fragmentActivity.supportFragmentManager.commit {
                add<ChatDetailFragment>(R.id.fragment_container, "", null)
                // add the transaction to the back stack so the user can navigate back
                addToBackStack(null)
            }*/

            val st: String = editTextTextInput.text.toString()
            roomId?.let { it1 -> sendMessage(st, it1) }
        }

        view.fab.setOnClickListener { view ->
            showCreateCategoryDialog()
        }

        (activity as AppCompatActivity?)!!.setSupportActionBar(view.toolbar)
        view.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    QiscusCore.clearUser()
                    (activity as AppCompatActivity?)!!.finish()
                }
            }
            true
        }

        val context = activity as Context
        model.getChatRoom().observe(viewLifecycleOwner, Observer<ChatRoomResult> {
            if (it.status.equals(ConstantVariable.ERROR)) {
                Utils.showToast(context, context.getString(R.string.username_not_found))
            } else {
                Utils.showToast(context, "chatroom : " + it.chatRoom?.id)
                println("email : " + it.chatRoom?.name)
                println("avatar : " + it.chatRoom?.avatarUrl)
                println("id 1 : " + it.chatRoom?.id)
                println("id 2 : " + it.chatRoom?.distinctId)
                println("id 3 : " + it.chatRoom?.uniqueId)
                roomId = it.chatRoom?.id
                QiscusPusherApi.getInstance().subscribeChatRoom(it.chatRoom)
            }
        })

        return view
    }

    private var roomId: Long? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun showCreateCategoryDialog() {
        val builder = AlertDialog.Builder(activity as Context)
        builder.setTitle("Enter your friend's email address")
        val view = layoutInflater.inflate(R.layout.dialog_new_contact, null)
        builder.setView(view)

        builder.setPositiveButton(android.R.string.ok) { dialog, p1 ->
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                val email: String = view.emailEditText.text.toString().trim()
                var isValid = true
                if (email.length == 0) {
                    view.emailEditText.error = getString(R.string.login_alert)
                    isValid = false
                }

                if (isValid) {
                    // do something
                    createChatRoom(email)
                    dialog.dismiss()
                }


            }
    }

    private fun createChatRoom(userId: String) {
        model.createChatRoom(userId)
    }


}