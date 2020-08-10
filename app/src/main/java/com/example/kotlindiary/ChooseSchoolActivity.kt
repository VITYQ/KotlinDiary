package com.example.kotlindiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kotlindiary.loginregister.ChooseFormActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_choose_school.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.schools_row.view.*
import java.text.FieldPosition

class ChooseSchoolActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_school)
        fetchSchools()
        button_add_school.setOnClickListener{
            val intent = Intent(this, AddSchoolActivity::class.java)
            startActivity(intent)
        }



    }

    private fun fetchSchools(){

        val ref = FirebaseDatabase.getInstance().getReference("/schools")

        Log.d("FireBaseLogging", "Here")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0 : DataSnapshot){
                val adapter = GroupAdapter<GroupieViewHolder>()
                Log.d("FireBaseLogging", "Still here")
                p0.children.forEach(){
                    //val name : school? = it.getValue(school::class.java)
                    val name = it.child("name").getValue().toString()
                    Log.d("FireBaseLogging", "name: $name")
                    Log.d("FireBaseLogging", "Value is :${it.child("name").getValue()}")
                    if(name != null){
                        adapter.add(SchoolItem(name))
                    }

                }

                adapter.setOnItemClickListener{item, view ->
                    val schoolName = item as SchoolItem
                    Log.d("FireBaseLogging", "Click on: ${schoolName.name.toString()}")
                    val uid = FirebaseAuth.getInstance().uid
                    val refuser = FirebaseDatabase.getInstance().getReference("/users/$uid")
                    refuser.child("school").setValue(schoolName.name)
                    val intent = Intent(this@ChooseSchoolActivity, ChooseFormActivity::class.java)
                    intent.putExtra("schoolName", schoolName.name)
                    startActivity(intent)
                }
                recyclerView_chooseSchool.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }
    class school(var name : String){
        constructor() : this("DSAF")
    }
//    class SchoolItem(var imya : String): Item<GroupieViewHolder>(){
//        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//            //Log.d("FireBaseLogging", "Binding, $name")
//            //viewHolder.itemView.textView_SchoolName.text = school.name.toString()
//            viewHolder.itemView.textView_SchoolName.text = imya
//
//        }
//
//        override fun getLayout(): Int {
//            Log.d("FireBaseLogging", "Загрузка layout")
//            return R.layout.schools_row
//        }
//    }

    class SchoolItem(val name : String) : Item<GroupieViewHolder>(){
        override fun getLayout () = R.layout.schools_row
        override fun bind (viewHolder: GroupieViewHolder, position: Int){
            viewHolder.itemView.textView_SchoolName.text = name
        }
    }




//    class LessonItem(val hometaskitem : AddLessonActivity.hometaskitem): Item<GroupieViewHolder>(){
//        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//            Log.d("FireBaseLogging", "ONE")
//            viewHolder.itemView.textView2.text = hometaskitem.lesson.toString()
//            viewHolder.itemView.textView.text = hometaskitem.hometask.toString()
//        }
//
//        override fun getLayout(): Int {
//            Log.d("FireBaseLogging", "TWO")
//            return R.layout.lesson_row
//        }
//    }
}
