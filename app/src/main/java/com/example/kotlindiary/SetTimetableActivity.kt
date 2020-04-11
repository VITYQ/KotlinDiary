package com.example.kotlindiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_add_lesson_to_timetable.*
import kotlinx.android.synthetic.main.activity_add_lesson_to_timetable.button_AddLessonToTimetable
import kotlinx.android.synthetic.main.activity_set_timetable.*
import kotlinx.android.synthetic.main.day_fragment.*
import kotlinx.android.synthetic.main.schools_row.view.*

class SetTimetableActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_timetable)
        val schoolName = intent.getStringExtra("schoolName")
        val form = intent.getStringExtra("form")
       // val number = intent.getIntExtra("nubmer")
        Log.d("DBLog", "STActivity. form : $form, schoolName : $schoolName,  number : $number")
        button_AddLessonToTimetable.setOnClickListener{
            val intent = Intent(this, AddLessonToTimetableActivity::class.java)
            intent.putExtra("schoolName", schoolName)
            intent.putExtra("form", form)
            //intent.putExtra("nubmer", number)
            startActivity(intent)
        }
        button_Next.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        fetchTimetable(schoolName, form)


    }


    private fun fetchTimetable(schoolName : String, form : String){
        val adapter = GroupAdapter<GroupieViewHolder>()
        val ref = FirebaseDatabase.getInstance().getReference("/schools/$schoolName/$form/timetable")
//        ref.addChildEventListener(object : ChildEventListener{
//            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//                val lesson = p0.child("lessonName").getValue().toString()
//                adapter.add(lessonItem(lesson))
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//
//            }
//
//            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//
//            }
//
//            override fun onChildRemoved(p0: DataSnapshot) {
//
//            }
//        })
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach(){
                    val lesson = it.child("lessonName").getValue().toString()
                    Log.d("DBLog", it.toString())
                    adapter.add(lessonItem(lesson))
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        recyclerView_Timetable.adapter = adapter



    }


    class lessonItem(val lessonName : String) : Item<GroupieViewHolder>(){
        override fun getLayout() = R.layout.schools_row
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.textView_SchoolName.text = lessonName
        }
    }



}
