package com.wahyu.testqiscus.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.wahyu.testqiscus.R
import com.wahyu.testqiscus.Utils
import com.wahyu.testqiscus.ui.fragment.ChatListFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Utils.updateStatusBar(window, this, R.color.colorStatusBar)
        if (fragment_container != null) {
            if (savedInstanceState != null) {
                return
            }

            supportFragmentManager.commit {
                add<ChatListFragment>(R.id.fragment_container, "", intent.extras)
            }
        }
    }

}

