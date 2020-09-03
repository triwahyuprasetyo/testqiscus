package com.wahyu.testqiscus.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.wahyu.testqiscus.R
import kotlinx.android.synthetic.main.fragment_chat_list.view.*

class ChatListFragment : Fragment() {

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
        val view: View = inflater.inflate(R.layout.fragment_chat_list, container, false)
        view.button.setOnClickListener {
            fragmentActivity.supportFragmentManager.commit {
                add<ChatDetailFragment>(R.id.fragment_container, "", null)
                // add the transaction to the back stack so the user can navigate back
                addToBackStack(null)
            }
        }
        return view
    }

}