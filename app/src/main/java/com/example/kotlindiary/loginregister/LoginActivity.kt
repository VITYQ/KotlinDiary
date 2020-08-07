package com.example.kotlindiary.loginregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.kotlindiary.MainActivity
import com.example.kotlindiary.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.button_Login
import kotlinx.android.synthetic.main.layout_bottom_sheet_set_timetable.*

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
        textInputLayout_PasswordLogin.editText?.doOnTextChanged { text, start, count, after ->
            textInputLayout_PasswordLogin.error = null
        }
        textInputLayout_UsernameLogin.editText?.doOnTextChanged { text, start, count, after ->
            textInputLayout_UsernameLogin.error = null
        }

    }

    private fun performSignIn(){
        val username = textInputLayout_UsernameLogin.editText?.text.toString()
        val password  = textInputLayout_PasswordLogin.editText?.text.toString()
        Log.d("ActivityLogin", "Username is $username")
        Log.d("ActivityLogin", "Password is $password")

        if(username.isEmpty() || password.isEmpty()) {
            if(username.isEmpty()){
                textInputLayout_UsernameLogin.error = "Введите email"
            }
            if(password.isEmpty()){
                textInputLayout_PasswordLogin.error = "Введите пароль"
            }
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

                    Toast.makeText(this, "Failure: ${it}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
