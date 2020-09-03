package com.wahyu.testqiscus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qiscus.sdk.chat.core.QiscusCore
import com.qiscus.sdk.chat.core.data.model.QiscusAccount

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("checkkkkk 1 : "+QiscusCore.hasSetupUser())
//        initQiscus()
        QiscusCore.clearUser();
        println("checkkkkk 2 : "+QiscusCore.hasSetupUser())

    }

    private fun initQiscus() {
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
    }
}

