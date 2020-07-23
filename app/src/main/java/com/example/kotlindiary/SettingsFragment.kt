package com.example.kotlindiary


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlindiary.loginregister.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.schools_row.view.*


/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    //TODO: разобраться с адаптером
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = GroupAdapter<GroupieViewHolder>()
        adapter.add(SettingsItem("Изменить расписание"))
        adapter.add(SettingsItem("Выйти"))
        adapter.setOnItemClickListener { item, view ->
            val setting = item as SettingsItem
            Log.d("checkingsettings", "${setting.name}")
            if(setting.name=="Выйти"){
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(context, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            else if(setting.name=="Изменить расписание"){
                val intent = Intent(context, SetTimetableActivity::class.java)
                startActivity(intent)
            }





        }
        settingsRecycler.adapter=adapter


    }


    class SettingsItem(val name : String) : Item<GroupieViewHolder>(){
        override fun getLayout () = R.layout.schools_row
        override fun bind (viewHolder: GroupieViewHolder, position: Int){
            viewHolder.itemView.textView_SchoolName.text = name
        }
    }


}
