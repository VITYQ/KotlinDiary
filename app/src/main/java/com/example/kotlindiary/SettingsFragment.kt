package com.example.kotlindiary


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.kotlindiary.loginregister.RegisterActivity
import com.example.kotlindiary.models.User
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.divider_row.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.profile_row.view.*
import kotlinx.android.synthetic.main.schools_row.view.*
import kotlinx.android.synthetic.main.settings_row.view.*


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
        adapter.add(profileItem())
        adapter.add(dividerItem("Настройки"))
        adapter.add(SettingsItem("Изменить расписание"))
        adapter.add(SettingsItem("Сменить школу"))
        adapter.add(SettingsItem("Изменить пароль (скоро)"))
        adapter.add(SettingsItem("Изменить электронную почту (скоро)"))
        adapter.add(SettingsItem("Уведомления (скоро)"))
        adapter.add(dividerLine())
        adapter.add(SettingsItem("О приложении"))
        adapter.add(SettingsItem("Выйти"))
        adapter.setOnItemClickListener { item, view ->
            if(item.viewType == R.layout.profile_row){
                Log.d("Checkset", "profile click")
            }
            else if(item.viewType == R.layout.settings_row){
                val setting = item as SettingsItem
                Log.d("Checkset", "${userMain.form}")
                Log.d("checkingsettings", "${setting.name}")
                if(setting.name != null){
                    if(setting.name=="Выйти"){
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(context, RegisterActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                    else if(setting.name=="Изменить расписание"){
                        val intent = Intent(context, SetTimetableActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                    else if(setting.name=="Сменить школу"){
                        val intent = Intent(context, ChooseSchoolActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                    else if(setting.name == "О приложении"){
                        val dialogInflater = LayoutInflater.from(context).inflate(R.layout.dialog_about, null)
                        val builder = AlertDialog.Builder(activity as MainActivity)
                            .setView(dialogInflater)
                            .setTitle("О нас")
                        //val view: View = inflater.inflate(android.R.layout.settings_row, null)
                        //builder.setMessage("Colibri 0.9 (alpha). Ваш школьный органайзер, в оформлении которого участвует весь коллектив. Легко делитесь домашним заданием и экономьте время с Colibri! ")
                        builder.setPositiveButton("Закрыть"){dialog, which ->
                            Toast.makeText(activity as MainActivity, "Спасибо, что пользуетесь моим приложением! <3", Toast.LENGTH_SHORT).show()
                        }
                        builder.create().show()
                    }
                }
            }

        }
        settingsRecycler.adapter=adapter


    }


    class SettingsItem(val name : String) : Item<GroupieViewHolder>(){
        override fun getLayout () = R.layout.settings_row
        override fun bind (viewHolder: GroupieViewHolder, position: Int){
            viewHolder.itemView.textView_SettingName.text = name
            if(name == "Изменить пароль (скоро)" || name == "Изменить электронную почту (скоро)" || name == "Уведомления (скоро)"){
                viewHolder.itemView.isEnabled = false
                viewHolder.itemView.textView_SettingName.setTextColor(Color.parseColor("#a1a1a1"))
            }
            if(name == "Выйти"){viewHolder.itemView.textView_SettingName.setTextColor(Color.parseColor("#D25550"))}
        }
    }
    class profileItem() : Item<GroupieViewHolder>(){
        override fun getLayout () = R.layout.profile_row
        override fun bind (viewHolder: GroupieViewHolder, position: Int){
            val name = userMain.name
            val surname = userMain.surname
            val form = userMain.form
            val email = userMain.email
            val school = userMain.school
            val username = userMain.username
            viewHolder.itemView.textView_NameSurrNameUsernameSettings.text = "$name $surname ($username)"
            viewHolder.itemView.textView_SchoolFormSettings.text = "$school, $form"
            viewHolder.itemView.textView_EmailSettings.text = "$email"
        }
    }
    class dividerItem(val name : String): Item<GroupieViewHolder>(){
        override fun getLayout() = R.layout.divider_row
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.textView_DividerName.text = name
        }
    }
    class dividerLine(): Item<GroupieViewHolder>(){
        override fun getLayout() = R.layout.divider_line
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {}
    }
}
