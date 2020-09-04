package com.wahyu.testqiscus.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.qiscus.sdk.chat.core.data.model.QiscusComment
import com.qiscus.sdk.chat.core.data.remote.QiscusApi
import com.wahyu.testqiscus.ConstantVariable
import com.wahyu.testqiscus.R
import com.wahyu.testqiscus.model.ContactData
import kotlinx.android.synthetic.main.fragment_chat_detail.view.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ChatDetailFragment : Fragment() {

    private lateinit var fragmentActivity: FragmentActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentActivity = context as FragmentActivity
    }

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

        var contactData: ContactData? = null
        val data: String? = arguments?.getString("data", "")
        data?.let {
            contactData = Gson().fromJson(it, ContactData::class.java)
        }
        view.toolbar.title = contactData?.name

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