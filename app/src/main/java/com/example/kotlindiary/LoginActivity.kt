package com.example.kotlindiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.button_Login
import kotlinx.android.synthetic.main.activity_register.editText_PasswordLogin
import kotlinx.android.synthetic.main.activity_register.editText_UsernameLogin

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

    button_Login.setOnClickListener(){
        val username = editText_UsernameLogin.text.toString()
        val password = editText_PasswordLogin.text.toString()
        Log.d("ActivityLogin", "Username is $username")
        Log.d("ActivityLogin", "Password is $password")
        if(username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите недостающие данные", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }
    }

    textView_BackToRegister.setOnClickListener(){
        finish()
    }

    }
}
