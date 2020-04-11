package com.example.kotlindiary.loginregister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.example.kotlindiary.R
import com.example.kotlindiary.models.User
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_new_form.*

class AddNewFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_form)
        val forms = arrayOf("А", "Б", "В", "Г")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, forms)
        spinner_Letter.adapter = adapter
        button.setOnClickListener{
            if(!editText.text.isEmpty()){
                UploadFormToFireBase()
            }
        }
        Log.d("check", "${spinner_Letter.selectedItem.toString()}")
    }


    private fun UploadFormToFireBase(){
        val ref = FirebaseDatabase.getInstance().getReference("/schools/${intent.getStringExtra("schoolName")}/${editText.text.toString()}${spinner_Letter.selectedItem.toString()}")
        ref.child("name").setValue("${editText.text.toString()}${spinner_Letter.selectedItem.toString()}")
    }
}
