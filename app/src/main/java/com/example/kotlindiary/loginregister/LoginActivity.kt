package com.example.kotlindiary.loginregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.kotlindiary.MainActivity
import com.example.kotlindiary.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.button_Login
import kotlinx.android.synthetic.main.activity_register.editText_PasswordLogin
import kotlinx.android.synthetic.main.activity_register.editText_UsernameLogin

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

    button_Login.setOnClickListener(){
        performSignIn()
    }

    textView_BackToRegister.setOnClickListener(){
        finish()
    }

    }

    private fun performSignIn(){
        val username = editText_UsernameLogin.text.toString()
        val password = editText_PasswordLogin.text.toString()
        Log.d("ActivityLogin", "Username is $username")
        Log.d("ActivityLogin", "Password is $password")
        if(username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите недостающие данные", Toast.LENGTH_SHORT).show()
            return
        }

        else{
            FirebaseAuth.getInstance().signInWithEmailAndPassword(username, password)
                .addOnCompleteListener{
                    if(!it.isSuccessful) return@addOnCompleteListener

                    val intent = Intent(this, MainActivity::class.java)

                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)

                    startActivity(intent)

                }
                .addOnFailureListener{
                    Toast.makeText(this, "Failure: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
