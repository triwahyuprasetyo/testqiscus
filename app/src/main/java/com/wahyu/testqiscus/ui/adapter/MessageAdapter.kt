package com.wahyu.testqiscus.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qiscus.sdk.chat.core.data.model.QiscusComment
import com.wahyu.testqiscus.R
import kotlinx.android.synthetic.main.message_receive_item_layout.view.*
import kotlinx.android.synthetic.main.message_send_item_layout.view.*


class MessageAdapter(
    private var listData: List<QiscusComment>,
    private val context: Context,
    private val receiverEmail: String
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    companion object {
        val SEND = 1
        val RECEIVE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            SEND -> ViewHolderItemSend(
                LayoutInflater.from(context)
                    .inflate(R.layout.message_send_item_layout, parent, false)
            )
            else -> ViewHolderItemReceive(
                LayoutInflater.from(context)
                    .inflate(R.layout.message_receive_item_layout, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        val qiscusComment: QiscusComment = listData[position]
        when (viewType) {
            SEND -> {
                val view = holder as ViewHolderItemSend
                view.textMessageSend.text = qiscusComment.message
            }
            else -> {
                val view = holder as ViewHolderItemReceive
                view.textMessageReceive.text = qiscusComment.message
            }
        }
    }

    override fun getItemCount(): Int = listData.size

    override fun getItemViewType(position: Int): Int {
        val viewType: QiscusComment = listData[position]
        if (viewType.senderEmail.equals(receiverEmail)) {
            return RECEIVE
        } else {
            return SEND
        }
    }

    open inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ViewHolderItemReceive(itemView: View) : ViewHolder(itemView) {
        val textMessageReceive: TextView = itemView.textMessageReceive
    }

    inner class ViewHolderItemSend(itemView: View) : ViewHolder(itemView) {
        val textMessageSend: TextView = itemView.textMessageSend
    }

}