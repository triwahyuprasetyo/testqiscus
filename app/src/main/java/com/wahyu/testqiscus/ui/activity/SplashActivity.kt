package com.wahyu.testqiscus.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.qiscus.sdk.chat.core.QiscusCore
import com.wahyu.testqiscus.R
import com.wahyu.testqiscus.Utils

class SplashActivity : AppCompatActivity() {
    val SPLASH_TIME: Long = 1500
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Utils.makeStatusBarTransparent(window, this)

        object : CountDownTimer(SPLASH_TIME, 1000) {
            override fun onTick(m: Long) {}
            override fun onFinish() {
                goto()
            }
        }.start()
    }

    fun goto() {
        var intent: Intent? = null
        if (QiscusCore.hasSetupUser()) {
            intent = Intent(this, MainActivity::class.java)
        } else {
            intent = Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

}