package com.example.kotlindiary


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlindiary.loginregister.RegisterActivity
import com.example.kotlindiary.models.User
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.divider_row.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.profile_row.view.*
import kotlinx.android.synthetic.main.schools_row.view.*
import kotlinx.android.synthetic.main.settings_row.view.*

lateinit var listenerForStudentStatus: ValueEventListener
var status = "0"

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    //TODO: разобраться с адаптером
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
        val settingsList = ArrayList<settingsFragmentData>()
        settingsList.add(settingsFragmentData(0, ""))
        settingsList.add(settingsFragmentData(2, "Настройки"))
        settingsList.add(settingsFragmentData(1, "Наш класс"))
        settingsList.add(settingsFragmentData(1, "Изменить расписание"))
        settingsList.add(settingsFragmentData(1, "Сменить школу"))
        settingsList.add(settingsFragmentData(1, "Изменить пароль класса"))
        settingsList.add(settingsFragmentData(1, "Изменить пароль (скоро)"))
        settingsList.add(settingsFragmentData(1, "Изменить электронную почту (скоро)"))
        settingsList.add(settingsFragmentData(1, "Уведомления (скоро)"))
        settingsList.add(settingsFragmentData(2, "Прочее"))
        settingsList.add(settingsFragmentData(1, "О приложении"))
        settingsList.add(settingsFragmentData(1, "Выйти"))


        val adapterForSettings = settingsAdapter(activity as MainActivity, settingsList)
        settingsRecycler.adapter = adapterForSettings

        if(userMain != null){
            val ref = FirebaseDatabase.getInstance().getReference("/schools/${userMain.school}/${userMain.form}/students/${userMain.uid}")
            listenerForStudentStatus = ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    status = p0.value.toString()
                    adapterForSettings.notifyDataSetChanged()
                }
                override fun onCancelled(p0: DatabaseError) {}
            })
        }
    }

    data class settingsFragmentData(val viewType: Int, val settingText : String)

    class settingsAdapter(val context : Context, val list : ArrayList<settingsFragmentData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        override fun getItemCount(): Int = list.size

        private inner class settingsItem(itemView: View) : RecyclerView.ViewHolder(itemView){
            fun bind(position: Int){
                itemView.textView_SettingName.text = list[position].settingText
                if(list[position].settingText == "Выйти"){
                    itemView.textView_SettingName.setTextColor(Color.parseColor("#D25550"))
                }
                else if(list[position].settingText == "Уведомления (скоро)" ||
                    list[position].settingText == "Изменить электронную почту (скоро)" ||
                    list[position].settingText == "Изменить пароль (скоро)" ||
                    list[position].settingText == "Изменить пароль класса"){
                    if(list[position].settingText == "Изменить пароль класса"){
                        itemView.textView_SettingName.text = "Изменить пароль класса (для старост)"
                    }
                    itemView.isEnabled = false
                    itemView.textView_SettingName.setTextColor(Color.parseColor("#a1a1a1"))
                }
                else{
                    itemView.isEnabled = true
                    itemView.textView_SettingName.setTextColor(Color.parseColor("#000000"))
                }

                if(list[position].settingText == "Изменить пароль класса" && status == "3"){
                    itemView.textView_SettingName.text = "Изменить пароль класса"
                    itemView.isEnabled = true
                    itemView.textView_SettingName.setTextColor(Color.parseColor("#000000"))
                }

                itemView.setOnClickListener {
                    val setting = itemView.textView_SettingName.text
                    if(setting != null){
                        if(setting == "Выйти"){
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("Выход")
                            builder.setMessage("Вы уверены, что хотите выйти из своего аккаунта?")
                            builder.setPositiveButton("Да"){dialog, which ->
                                FirebaseAuth.getInstance().signOut()
                                val intent = Intent(context, RegisterActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(context, intent, null)
                            }
                            builder.setNegativeButton("Нет"){dialog, which ->
                            }
                            builder.create().show()

                        }
                        else if(setting == "Изменить расписание"){
                            val intent = Intent(context, SetTimetableActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(context, intent, null)
                        }
                        else if(setting == "Сменить школу"){
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("Сменить школу")
                            builder.setMessage("После смены школы права старосты будут утрачены")
                            builder.setPositiveButton("Да"){dialog, which ->
                                val intent = Intent(context, ChooseSchoolActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(context, intent, null)
                            }
                            builder.setNegativeButton("Нет"){dialog, which ->
                            }
                            builder.create().show()
                        }
                        else if(setting == "О приложении"){
                            val dialogInflater = LayoutInflater.from(context).inflate(R.layout.dialog_about, null)
                            val builder = AlertDialog.Builder(context)
                                .setView(dialogInflater)
                                .setTitle("О нас")
                            builder.setPositiveButton("Закрыть"){dialog, which ->
                                Toast.makeText(context, "Спасибо, что пользуетесь моим приложением! <3", Toast.LENGTH_SHORT).show()
                            }
                            builder.create().show()
                        }
                        else if(setting == "Изменить пароль класса"){
                            val dialogInflater = LayoutInflater.from(context).inflate(R.layout.dialog_enter_form_password, null)
                            val passEditText = dialogInflater.findViewById(R.id.textInputLayout_EnterFormPassword) as TextInputLayout
                            val builder = AlertDialog.Builder(context)
                                .setView(dialogInflater)
                                .setTitle("Смена пароля класса")
                            builder.setPositiveButton("Далее"){dialog, which ->
                                if(passEditText.editText?.text.toString() != ""){
                                    val password = passEditText.editText?.text.toString()
                                    val ref = FirebaseDatabase.getInstance().getReference("/schools/${userMain.school}/${userMain.form}")
                                    ref.child("password").setValue(password)
                                    Toast.makeText(context, "Вы успешно сменили пароль класса!", Toast.LENGTH_LONG)
                                }
                                else{
                                    Toast.makeText(context, "Пароль не может быть пустым", Toast.LENGTH_LONG).show()
                                }
                            }
                            builder.setNegativeButton("Закрыть"){dialog, which ->

                            }
                            builder.create().show()
                        }

                    }
                }
            }
        }
        private inner class profileItem(itemView: View) : RecyclerView.ViewHolder(itemView){
            fun bind(position: Int){
                val name = userMain.name
                val surname = userMain.surname
                val form = userMain.form
                val email = userMain.email
                val school = userMain.school
                val username = userMain.username
                itemView.textView_NameSurrNameUsernameSettings.text = "$name $surname ($username)"
                itemView.textView_SchoolFormSettings.text = "$school, $form"
                itemView.textView_EmailSettings.text = "$email"
            }
        }
        private inner class dividerItem(itemView: View) : RecyclerView.ViewHolder(itemView){
            fun bind(position: Int){
                itemView.textView_DividerName.text = list[position].settingText
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            // 0 - профиль
            // 1 - настройка
            // 2 - разделитель
            if(viewType == 0){return profileItem(LayoutInflater.from(context).inflate(R.layout.profile_row, parent, false))}
            else if(viewType == 2){return dividerItem(LayoutInflater.from(context).inflate(R.layout.divider_row,parent, false))}
            return settingsItem(LayoutInflater.from(context).inflate(R.layout.settings_row, parent, false))
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(list[position].viewType == 0){(holder as profileItem).bind(position)}
            else if(list[position].viewType == 1){(holder as settingsItem).bind(position)}
            else if(list[position].viewType == 2){(holder as dividerItem).bind(position)}
        }

        override fun getItemViewType(position: Int): Int {
            return list[position].viewType
        }

    }

}
