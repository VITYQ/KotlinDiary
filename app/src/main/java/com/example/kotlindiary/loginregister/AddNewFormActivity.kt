package com.example.kotlindiary.loginregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.kotlindiary.R
import com.example.kotlindiary.SetTimetableActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_new_form.*

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
        button.setOnClickListener{
            if(textInputLayout_Form.editText?.text.toString() != ""){
                UploadFormToFireBase()
            }
        }
        //Log.d("check", "${spinner_Letter.selectedItem.toString()}")
    }


    private fun UploadFormToFireBase(){
        val ref = FirebaseDatabase.getInstance().getReference("/schools/$schoolName/${textInputLayout_Form.editText?.text.toString()}")
        ref.child("name").setValue("${textInputLayout_Form.editText?.text.toString()}")
            .addOnSuccessListener {

                Log.d("DBLoggging", "sch: ${intent.getStringExtra("schoolName")}, form: ${textInputLayout_Form.editText?.text.toString()}")
                val uid = FirebaseAuth.getInstance().uid
                val refusr = FirebaseDatabase.getInstance().getReference("/users/$uid")
                refusr.child("form").setValue("${textInputLayout_Form.editText?.text.toString()}")
                    .addOnSuccessListener {
                        val intent = Intent(this, SetTimetableActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("schoolName", schoolName)
                        intent.putExtra("form", "${textInputLayout_Form.editText?.text.toString()}")
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
                    }

            }
    }
}
