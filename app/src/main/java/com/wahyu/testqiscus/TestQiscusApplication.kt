package com.wahyu.testqiscus

import android.app.Application
import com.qiscus.sdk.chat.core.QiscusCore

class TestQiscusApplication : Application() {

    private val APPID: String = "sdksample"

    override fun onCreate() {
        super.onCreate()
        QiscusCore.setup(this, APPID)
    }
}