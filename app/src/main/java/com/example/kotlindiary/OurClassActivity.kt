package com.example.kotlindiary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.LayoutDirection
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlindiary.loginregister.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_our_class.*
import kotlinx.android.synthetic.main.divider_row.view.*
import kotlinx.android.synthetic.main.lesson_row.view.*
import kotlinx.android.synthetic.main.student_row.view.*
import java.util.*
import kotlin.collections.ArrayList

lateinit var refForGetUid : DatabaseReference
lateinit var listenerForGetUid : ValueEventListener
lateinit var refForUsers : DatabaseReference
lateinit var listenerForUsers : ValueEventListener
var intentStatus = ""

class OurClassActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_our_class)
        ToolBar_Class.setNavigationOnClickListener {
            onBackPressed()
        }
        intentStatus = intent.getStringExtra("status")!!
        val context = this
        var list = ArrayList<Student>()
        val adapter = SchoolAdapter(context, list)
        refForGetUid = FirebaseDatabase.getInstance().getReference("/schools/${userMain.school}/${userMain.form}/students")
        listenerForGetUid = refForGetUid.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                list.clear()
                p0.children.forEach {
                    val uid = it.key
                    val status = it.value.toString()
                    refForUsers = FirebaseDatabase.getInstance().getReference("/users/$uid")
                    listenerForUsers = refForUsers.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {

                            val name = p0.child("name").value.toString()
                            val surname = p0.child("surname").value.toString()
                            Log.d ("logforstudents", "$name $surname $status")
                            if(uid != null){
                                list.add(Student(uid, name, surname, status))
                            }
                            adapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(p0: DatabaseError) {}
                    })

                }
                recyclerView_students.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    override fun onPause() {
        refForGetUid.removeEventListener(listenerForGetUid)
        refForUsers.removeEventListener(listenerForUsers)
        super.onPause()
    }
    data class Student(val uid : String, val name : String, val surname : String, val status : String)

    class SchoolAdapter(val context : Context, val students : ArrayList<Student>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        override fun getItemCount(): Int = students.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return studentItem(LayoutInflater.from(context).inflate(R.layout.student_row, parent, false))
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val name = students[position].name
            val surname = students[position].surname
            var status = students[position].status
            when (status){
                "0" -> {
                    status = "Бан"
                    holder.itemView.textView_StudentStatus.setTextColor(Color.parseColor("#D25550"))
                }
                "1" -> {
                    status = "Ученик"
                    holder.itemView.textView_StudentStatus.setTextColor(Color.parseColor("#a1a1a1"))
                }
                "2" -> status = "Модератор"
                "3" -> {
                    status = "Староста"
                    holder.itemView.textView_StudentStatus.setTextColor(Color.parseColor("#3EA34A"))
                }
            }
            holder.itemView.textView_StudentNameSurname.text = "$name $surname"
            holder.itemView.textView_StudentStatus.text = status

            holder.itemView.setOnClickListener {
                Log.d("ergk", "clicked $status")
                if (intentStatus == "3"){
                    if(students[position].uid == FirebaseAuth.getInstance().uid){
                        Toast.makeText(context, "Вы не можете изменить права себе", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Выбор судьбы ученика")
                        builder.setMessage("Старосты могут банить учеников и менять пароль класса. В бане ученик не сможет редактировать записи в расписании. Ученики могут только редактировать домашние задания в расписании")
                        builder.setPositiveButton("Староста"){dialog, which ->
                            val uid = students[position].uid
                            val ref = FirebaseDatabase.getInstance().getReference("/schools/${userMain.school}/${userMain.form}/students")
                            ref.child("$uid").setValue(3)
                        }
                        builder.setNegativeButton("Ученик"){dialog, which ->
                            val uid = students[position].uid
                            val ref = FirebaseDatabase.getInstance().getReference("/schools/${userMain.school}/${userMain.form}/students")
                            ref.child("$uid").setValue(1)
                        }
                        builder.setNeutralButton("Забанить"){dialog, which ->
                            val uid = students[position].uid
                            val ref = FirebaseDatabase.getInstance().getReference("/schools/${userMain.school}/${userMain.form}/students")
                            ref.child("$uid").setValue(0)
                        }
                        builder.create().show()
                    }

                }
                else{
                    Toast.makeText(context, "Изменять привелегии пока могут только старосты", Toast.LENGTH_SHORT).show()
                }

            }
        }
        private inner class studentItem(itemView: View) : RecyclerView.ViewHolder(itemView){

        }
    }

}