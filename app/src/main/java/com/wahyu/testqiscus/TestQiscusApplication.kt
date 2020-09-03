package com.wahyu.testqiscus

import android.app.Application
import com.qiscus.sdk.chat.core.QiscusCore
import com.wahyu.testqiscus.ConstantVariable.APPID

class TestQiscusApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        QiscusCore.setup(this, APPID)
    }
}