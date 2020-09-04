package com.wahyu.testqiscus.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.wahyu.testqiscus.ConstantVariable.MINIMUM_CHAR
import com.wahyu.testqiscus.ConstantVariable.SUCCESS
import com.wahyu.testqiscus.R
import com.wahyu.testqiscus.Utils
import com.wahyu.testqiscus.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val model: LoginViewModel by viewModels()
        model.getStatus().observe(this, Observer<String> {
            if (it.equals(SUCCESS)) {
                gotoMain()
            } else {
                Utils.showToast(this, it)
            }
        })


        buttonLogin.setOnClickListener {
            val email: String = textInputEditTextEmail.text.toString().trim()
            val displayName: String = textInputEditTextDisplayName.text.toString().trim()
            if (email.length > MINIMUM_CHAR && displayName.length > MINIMUM_CHAR) {
                model.login(email, displayName)
            } else {
                Utils.showToast(this, getString(R.string.login_alert))
            }
        }
    }

    fun gotoMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}