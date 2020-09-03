package com.wahyu.testqiscus.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.qiscus.sdk.chat.core.QiscusCore
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

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}