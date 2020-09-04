package com.wahyu.testqiscus.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom
import com.wahyu.testqiscus.R
import com.wahyu.testqiscus.ui.fragment.ChatListFragment
import kotlinx.android.synthetic.main.contact_item_layout.view.*

class ContactAdapter(
    private var listQiscusChatRoom: List<QiscusChatRoom>,
    private val callback: ChatListFragment.OnItemClickListener,
    private val context: Context
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolderItemContact(
            LayoutInflater.from(context).inflate(R.layout.contact_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val qiscusChatRoom: QiscusChatRoom = listQiscusChatRoom[position]
        val view = holder as ViewHolderItemContact
        view.contactName.text = qiscusChatRoom.name
        view.contactMessage.text = qiscusChatRoom.lastComment?.message
        view.container.setOnClickListener {
            callback.onSelectCandidate(position)
        }

        Glide.with(context).load(qiscusChatRoom.avatarUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_broken_image)
            .into(view.imageAvatar)
    }

    override fun getItemCount(): Int = listQiscusChatRoom.size

    open inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ViewHolderItemContact(itemView: View) : ViewHolder(itemView) {
        val container: ConstraintLayout = itemView.container
        val imageAvatar: ImageView = itemView.imageViewAvatar
        var contactName: TextView = itemView.textViewName
        val contactMessage: TextView = itemView.textViewMessage
    }

}