package com.example.kotlindiary.loginregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.example.kotlindiary.R
import com.example.kotlindiary.SetTimetableActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_new_form.*

class AddNewFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_form)
        val forms = arrayOf("А", "Б", "В", "Г")
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
        val ref = FirebaseDatabase.getInstance().getReference("/schools/${intent.getStringExtra("schoolName")}/${textInputLayout_Form.editText?.text.toString()}")
        ref.child("name").setValue("${textInputLayout_Form.editText?.text.toString()}")
            .addOnSuccessListener {
                val intent = Intent(this, SetTimetableActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("schoolName", intent.getStringExtra("schoolName"))
                startActivity(intent)
            }
    }
}
