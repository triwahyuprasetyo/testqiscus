package com.wahyu.testqiscus.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.qiscus.sdk.chat.core.QiscusCore
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom
import com.qiscus.sdk.chat.core.data.remote.QiscusPusherApi
import com.qiscus.sdk.chat.core.event.QiscusCommentReceivedEvent
import com.wahyu.testqiscus.ConstantVariable
import com.wahyu.testqiscus.R
import com.wahyu.testqiscus.Utils
import com.wahyu.testqiscus.model.ChatRoomResult
import com.wahyu.testqiscus.ui.adapter.ContactAdapter
import com.wahyu.testqiscus.viewmodel.ChatListViewModel
import kotlinx.android.synthetic.main.dialog_new_contact.view.*
import kotlinx.android.synthetic.main.fragment_chat_list.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class ChatListFragment : Fragment() {

    private lateinit var fragmentActivity: FragmentActivity
    private val model: ChatListViewModel by viewModels()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapterRecyclerView: ContactAdapter
    private lateinit var qiscusChatRoomList: MutableList<QiscusChatRoom>
    private val callback = object : OnItemClickListener {
        override fun onSelectCandidate(idx: Int) {
            goToDetail(qiscusChatRoomList.get(idx))
        }
    }

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

    interface OnItemClickListener {
        fun onSelectCandidate(id: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_chat_list, container, false)

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

        qiscusChatRoomList=mutableListOf<QiscusChatRoom>()

        var tempChatRoomList: List<QiscusChatRoom?> = QiscusCore.getDataStore().getChatRooms(10)
        for (i in 0 until tempChatRoomList.size) {
            val qiscusChatRoom: QiscusChatRoom? = tempChatRoomList.get(i)
            qiscusChatRoom?.let { qiscusChatRoomList.add(it) }
            QiscusPusherApi.getInstance().subscribeChatRoom(qiscusChatRoom)
        }

        val context = activity as Context
        model.getChatRoom().observe(viewLifecycleOwner, Observer<ChatRoomResult> {
            if (it.status.equals(ConstantVariable.ERROR)) {
                Utils.showToast(context, context.getString(R.string.username_not_found))
            } else {
                it.chatRoom?.let {
                    QiscusCore.getDataStore().addOrUpdate(it)
                    QiscusPusherApi.getInstance().subscribeChatRoom(it)
                    qiscusChatRoomList.add(it)
                    goToDetail(it)
                }
            }
        })

        linearLayoutManager = LinearLayoutManager(context)
        view.recyclerView.layoutManager = linearLayoutManager
        adapterRecyclerView =
            ContactAdapter(
                listQiscusChatRoom = qiscusChatRoomList,
                callback = callback,
                context = context
            )
        view.recyclerView.adapter = adapterRecyclerView

        return view
    }

    fun goToDetail(qiscusChatRoom: QiscusChatRoom) {
        fragmentActivity.supportFragmentManager.commit {
            add<ChatDetailFragment>(
                R.id.fragment_container,
                "",
                bundleOf("qiscusChatRoom" to qiscusChatRoom)
            )
            addToBackStack(null)
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
                    createChatRoom(email)
                    dialog.dismiss()
                }
            }
    }

    private fun createChatRoom(userId: String) {
        model.createChatRoom(userId)
    }

}