package com.wahyu.testqiscus.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.wahyu.testqiscus.R
import com.wahyu.testqiscus.ui.fragment.ChatListFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (fragment_container != null) {
            if (savedInstanceState != null) {
                return
            }

            supportFragmentManager.commit {
                add<ChatListFragment>(R.id.fragment_container, "", intent.extras)
            }
        }

        /*println("checkkkkk 1 : "+QiscusCore.hasSetupUser())
//        initQiscus()
        QiscusCore.clearUser();
        println("checkkkkk 2 : "+QiscusCore.hasSetupUser())*/

    }

    /*private fun initQiscus() {
        QiscusCore.setUser("triwahyuprasetyo@gmail.com", "qiscuschat101")
                .withUsername("triwahyuprasetyo")
                .withAvatarUrl("")
                .withExtras(null)
                .save(object : QiscusCore.SetUserListener {
                    override fun onSuccess(p0: QiscusAccount?) {
                        println("okayyy yessssss")
                    }

                    override fun onError(p0: Throwable?) {
                        println("okayyy nooooooo")
                    }
                })
    }*/
}

