package com.example.kotlindiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kotlindiary.loginregister.ChooseFormActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_school.*
import java.util.*

class AddSchoolActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_school)
        button_Add.setOnClickListener{
            val name = textInputLayout_SchoolName.editText?.text.toString()
            if(name.isEmpty()){
                Toast.makeText(this, "Введите название школы", Toast.LENGTH_SHORT).show()
            }
            else{
                AddSchoolToFirebase(name)
            }
        }
    }
    private fun AddSchoolToFirebase(name : String){
        //val id = editText_SchoolName.text.toString()
        val id = textInputLayout_SchoolName.editText?.text.toString()
        val ref = FirebaseDatabase.getInstance().getReference("/schools/$id")
        ref.child("name").setValue(name)
            .addOnSuccessListener {

                val uid = FirebaseAuth.getInstance().uid
                val refusr = FirebaseDatabase.getInstance().getReference("/users/$uid")
                refusr.child("school").setValue(id)

                val intent = Intent(this, ChooseFormActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("schoolName", id)
                startActivity(intent)


            }

    }
}
