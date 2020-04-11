package com.example.kotlindiary.loginregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.kotlindiary.ChooseSchoolActivity
import com.example.kotlindiary.MainActivity
import com.example.kotlindiary.R
import com.example.kotlindiary.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        button_Next.setOnClickListener{
            val name = editText_Name.text.toString()
            val surname = editText_Surname.text.toString()
            if(name.isEmpty() || surname.isEmpty()){
                Toast.makeText(this, "Атата, введи недостающие данные и продолжим ;)", Toast.LENGTH_SHORT).show()
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
        ref.child("name").setValue(editText_Name.text.toString())
        ref.child("surname").setValue(editText_Surname.text.toString())
            .addOnSuccessListener {
                Log.d("ProfileActivity", "Success added name and surname to Firebase")
                val intent = Intent(this, ChooseSchoolActivity::class.java)
                startActivity(intent)
            }


//        ref.setValue(userDat)
//            .addOnSuccessListener {
//                Log.d("ProfileActivity", "Success added name and surname to Firebase")
//                val intent = Intent(this, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                startActivity(intent)
//            }
//        ref.setValue(user)
//            .addOnSuccessListener {
//                Log.d("RegisterActivity", "Finally complete registration and save user to database")
//                val intent = Intent(this, ProfileActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//            }
//            .addOnFailureListener{
//                Log.d("RegisterActivity", "Something has gone wrong")
//            }
    }

}
