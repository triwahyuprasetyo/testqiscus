package com.wahyu.testqiscus.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wahyu.testqiscus.R
import com.wahyu.testqiscus.model.ContactData
import com.wahyu.testqiscus.ui.fragment.ChatListFragment

class ContactAdapter(
    private var listData: List<ContactData>,
    private val callback: ChatListFragment.OnItemClickListener,
    private val context: Context
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    fun replaceData(list: List<ContactData>) {
        listData = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolderItemContact(
            LayoutInflater.from(context).inflate(R.layout.contact_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contactData: ContactData = listData[position]
        val view = holder as ViewHolderItemContact

        view.contactName.text = contactData.name
        view.contactMessage.text = contactData.message


        /*val viewHolderCandidate = holder as ViewHolderItemCandidate
        val profileAndJob: ProfileAndJob = listData[position] as ProfileAndJob
        viewHolderCandidate.cardView.setOnClickListener {
            profileAndJob.profileCandidate.id.let { it -> callback.onSelectCandidate(it) }
        }
        viewHolderCandidate.candidateName.text = profileAndJob.profileCandidate.name
        viewHolderCandidate.candidateStatus.text =
            profileAndJob.jobDescriptionCandidate.status
        viewHolderCandidate.candidateJobDescription.text =
            profileAndJob.jobDescripticandidate_item_layout.xmlonCandidate.job_title

        Glide.with(context).load(profileAndJob.profileCandidate.photo)
            .circleCrop()
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .error(R.drawable.ic_baseline_account_circle_24)
            .into(viewHolderCandidate.imageAvatar)*/
    }

    override fun getItemCount(): Int = listData.size

    open inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ViewHolderItemContact(itemView: View) : ViewHolder(itemView) {

        val imageAvatar: ImageView = itemView.findViewById(R.id.imageViewAvatar)
        var contactName: TextView = itemView.findViewById(R.id.textViewName)
        val contactMessage: TextView = itemView.findViewById(R.id.textViewMessage)
    }

}