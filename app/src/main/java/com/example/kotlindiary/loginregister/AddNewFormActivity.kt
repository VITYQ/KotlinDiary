package com.example.kotlindiary.loginregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.kotlindiary.R
import com.example.kotlindiary.SetTimetableActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_new_form.*
import kotlinx.android.synthetic.main.activity_register.*

class AddNewFormActivity : AppCompatActivity() {
var schoolName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_form)
        val forms = arrayOf("А", "Б", "В", "Г")
        schoolName = intent.getStringExtra("schoolName")
        Log.d("DBLoggging", "sch1: ${intent.getStringExtra("schoolName")}")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, forms)
        //spinner_Letter.adapter = adapter

        textInputLayout_Form.editText?.doOnTextChanged { text, start, count, after ->
            textInputLayout_Form.error = null
        }
        textInputLayout_FormPassword.editText?.doOnTextChanged { text, start, count, after ->
            textInputLayout_FormPassword.error = null
        }
        textInputLayout_FormPasswordConfirm.editText?.doOnTextChanged { text, start, count, after ->
            textInputLayout_FormPasswordConfirm.error = null
        }



        button.setOnClickListener{

                val form = textInputLayout_Form.editText?.text.toString()
                val password = textInputLayout_FormPassword.editText?.text.toString()
                val confirm = textInputLayout_FormPasswordConfirm.editText?.text.toString()


                if(form.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                    if(form.isEmpty()){textInputLayout_Form.error = "Введите email"}
                    if(password.isEmpty()){textInputLayout_FormPassword.error = "Введите имя пользователя"
                    textInputLayout_FormPassword.contentDescription = "sssss"}
                    if(confirm.isEmpty()){textInputLayout_FormPasswordConfirm.error = "Введите пароль"}
                }
                else{
                    if(password == confirm){
                        UploadFormToFireBase(password, form)
                    }
                    else{
                        Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                        textInputLayout_FormPasswordConfirm.error = "Не совпадают пароли"
                    }
                    //UploadFormToFireBase()
                }

        }
        //Log.d("check", "${spinner_Letter.selectedItem.toString()}")
    }


    private fun UploadFormToFireBase(password : String, form : String){
        val ref = FirebaseDatabase.getInstance().getReference("/schools/$schoolName/$form")
        ref.child("password").setValue(password)
        ref.child("name").setValue(form)
            .addOnSuccessListener {
                val uid = FirebaseAuth.getInstance().uid
                val refStudents = FirebaseDatabase.getInstance().getReference("schools/$schoolName/$form/students")
                refStudents.child("$uid").setValue(3)
                Log.d("DBLoggging", "sch: ${intent.getStringExtra("schoolName")}, form: ${textInputLayout_Form.editText?.text.toString()}")
                val refusr = FirebaseDatabase.getInstance().getReference("/users/$uid")
                refusr.child("form").setValue(form)
                    .addOnSuccessListener {
                        val intent = Intent(this, SetTimetableActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("schoolName", schoolName)
                        intent.putExtra("form", form)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
                    }

            }
    }
}
