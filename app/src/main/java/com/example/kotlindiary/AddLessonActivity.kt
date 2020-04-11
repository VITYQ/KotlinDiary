package com.example.kotlindiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.widget.Toast
import com.example.kotlindiary.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_lesson.*
import java.util.*

class AddLessonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lesson)


        button_Add.setOnClickListener(){
            val hometask = editText_hometask.text.toString()
            val lesson = editText_lesson.text.toString()
            //var schoolName : String
            if(hometask != null && lesson != null){
                val uid = FirebaseAuth.getInstance().uid.toString()
                val refschool = FirebaseDatabase.getInstance().getReference("/users/$uid")
                refschool.addListenerForSingleValueEvent(object  : ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot){
//                        val user = p0.getValue(User::class.java)
//                        schoolName = user?.school.toString()
//                        Log.d("FireBaseLogging", "User: ${schoolName}")
                        val schoolName = p0.getValue(User::class.java)?.school.toString()
                        Log.d("FireBaseLogging", "User: ${schoolName}")
                        val date = "${Date().year}-${Date().month}-${Date().date}"
                        val name = UUID.randomUUID().toString()
                        val ref = FirebaseDatabase.getInstance().getReference("/schools/$schoolName/hometasks/$date/$name")
                        val upload = hometaskitem(lesson, hometask, uid)
                        ref.setValue(upload)
                            .addOnSuccessListener {
                                finish()
                            }
                            .addOnFailureListener{
                                Toast.makeText(this@AddLessonActivity, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
                            }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }
                })


            }
        }
    }
    class hometaskitem(val lesson : String, val hometask : String, val fromId : String){
        constructor() : this ("","","")
    }
}
