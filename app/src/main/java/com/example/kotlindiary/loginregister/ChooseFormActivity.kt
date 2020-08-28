package com.example.kotlindiary.loginregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.kotlindiary.ChooseSchoolActivity
import com.example.kotlindiary.MainActivity
import com.example.kotlindiary.R
import com.example.kotlindiary.SetTimetableActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_choose_form.*
import kotlinx.android.synthetic.main.dialog_enter_form_password.*
import kotlinx.android.synthetic.main.dialog_enter_form_password.view.*
import kotlinx.android.synthetic.main.schools_row.view.*

class ChooseFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_form)

        val schoolName = intent.getStringExtra("schoolName")
        Log.d("DBLoggging", "Get intent $schoolName")
        button2.setOnClickListener{
            val intent = Intent(this, AddNewFormActivity::class.java)
            intent.putExtra("schoolName", schoolName)
            startActivity(intent)
        }
        FetchForms(schoolName)

    }


    private fun FetchForms(schoolName : String){
        //val adapter = GroupAdapter<GroupieViewHolder>()
        Log.d("ChooseFormActivityLog", "$schoolName")
        val ref = FirebaseDatabase.getInstance().getReference("/schools/$schoolName/")

        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                p0.children.forEach(){

                    val formName = it.child("name").getValue().toString()
                    //val formName = it.key
                    Log.d("ChooseFormActivityLog", it.toString())

                    Log.d("ChooseFormActivityLog", "Added : $formName")
                    if(formName!="null" ){
                        adapter.add(FormItem(formName))
                    }



                }

                adapter.setOnItemClickListener{ item, view ->
                    val formName = item as FormItem
                    val uid = FirebaseAuth.getInstance().uid
                    val form = formName.formName
                    val dialogInflater = LayoutInflater.from(this@ChooseFormActivity).inflate(R.layout.dialog_enter_form_password, null)
                    val passEditText = dialogInflater.findViewById(R.id.textInputLayout_EnterFormPassword) as TextInputLayout
                    val formPassword = passEditText.editText?.text.toString()
                    val builder = AlertDialog.Builder(this@ChooseFormActivity)
                        .setView(dialogInflater)
                        .setTitle("Введите пароль класса")
                        .setNegativeButton("Закрыть"){dialog, which ->

                        }
                        .setPositiveButton("Далее"){dialog, which ->
                            val ref = FirebaseDatabase.getInstance().getReference("/schools/$schoolName/$form")
                            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(p0: DataSnapshot) {
                                    val password = p0.child("password").value
                                    if(passEditText.editText?.text.toString() == password){
                                        val refuser = FirebaseDatabase.getInstance().getReference("/users/$uid")
                                        refuser.child("form").setValue(form)
                                            .addOnSuccessListener {
                                                val intent = Intent(this@ChooseFormActivity, SetTimetableActivity::class.java)
                                                intent.putExtra("schoolName", schoolName)
                                                intent.putExtra("form", form)
                                                startActivity(intent)
                                            }

                                    }
                                    else{
                                        Toast.makeText(this@ChooseFormActivity, "Неправильный пароль", Toast.LENGTH_SHORT).show()
                                    }
                                    Log.d("passcheck", password.toString())
                                }
                                override fun onCancelled(p0: DatabaseError) {

                                }
                            })
                        }
                    //Log.d("lolz", passEditText.editText?.text.toString())
                    builder.create().show()



                }
                recyclerView_Forms.adapter = adapter


            }

            override fun onCancelled(p0: DatabaseError) {

            }
            //recyclerView_Forms.adapter = adapter
        })


    }

    override fun onBackPressed() {
        val intent = Intent(this, ChooseSchoolActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        //zsuper.onBackPressed()
    }


    class FormItem(val formName : String) : Item<GroupieViewHolder>(){
        override fun getLayout () = R.layout.schools_row
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.textView_SchoolName.text = formName.toString()
        }

    }
}
