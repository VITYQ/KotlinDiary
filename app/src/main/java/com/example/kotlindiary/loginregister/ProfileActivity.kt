package com.example.kotlindiary.loginregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.kotlindiary.ChooseSchoolActivity
import com.example.kotlindiary.MainActivity
import com.example.kotlindiary.R
import com.example.kotlindiary.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_set_timetable.*


class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        textInputLayout_Name.editText?.doOnTextChanged { text, start, count, after ->
            textInputLayout_Name.error = null
        }
        textInputLayout_Surname.editText?.doOnTextChanged { text, start, count, after ->
            textInputLayout_Surname.error = null
        }

        button_Next.setOnClickListener{
            val name = textInputLayout_Name.editText?.text.toString()
            val surname = textInputLayout_Surname.editText?.text.toString()
            if(name.isEmpty() || surname.isEmpty()){
                if(name.isEmpty()){textInputLayout_Name.error = "Введите имя"}
                if(surname.isEmpty()){textInputLayout_Surname.error = "Введите фамилию"}
                return@setOnClickListener
            }
            else {
                saveUserDataToFirebase()
            }
        }
    }
    private fun saveUserDataToFirebase(){
        val uid = FirebaseAuth.getInstance().uid ?: ""

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.child("name").setValue(textInputLayout_Name.editText?.text.toString())
        ref.child("surname").setValue(textInputLayout_Surname.editText?.text.toString())
            .addOnSuccessListener {
                Log.d("ProfileActivity", "Success added name and surname to Firebase")
                val intent = Intent(this, ChooseSchoolActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
            }
    }

}
