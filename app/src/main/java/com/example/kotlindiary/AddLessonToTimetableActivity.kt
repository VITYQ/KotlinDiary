package com.example.kotlindiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_lesson_to_timetable.*


public var number : Int = 1

class AddLessonToTimetableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lesson_to_timetable)
        val form = intent.getStringExtra("form")
        val schoolName = intent.getStringExtra("schoolName")
        //var number = intent.getIntExtra("nubmer")

        Log.d("DBLog", "Before listener. Form : $form, schoolName : $schoolName, number : $number")
        button_AddLessonToTimetable.setOnClickListener{
            if(!editText_LessonName.text.isEmpty()){
                Log.d("DBLog", "Form : $form, schoolName : $schoolName")
                val ref = FirebaseDatabase.getInstance().getReference("/schools/$schoolName/$form/timetable/$number")
                ref.child("lessonName").setValue(editText_LessonName.text.toString())
                val intent = Intent(this, SetTimetableActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("schoolName", schoolName)
                intent.putExtra("form", form)
                number++
                startActivity(intent)
            }
        }
    }
}
