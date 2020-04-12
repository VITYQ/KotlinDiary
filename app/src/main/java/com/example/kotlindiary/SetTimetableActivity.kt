package com.example.kotlindiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout.MODE_SCROLLABLE
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_add_lesson_to_timetable.*
import kotlinx.android.synthetic.main.activity_add_lesson_to_timetable.button_AddLessonToTimetable
import kotlinx.android.synthetic.main.activity_set_timetable.*
import kotlinx.android.synthetic.main.day_fragment.*
import kotlinx.android.synthetic.main.day_fragment.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.schools_row.view.*

class SetTimetableActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_timetable)




        floatac.setOnClickListener{
            Log.d("clicked", "clicked")
        }


        val string = arrayOf<String>("dgsg", "fsdgdsfg")
        val adapter = ViewPager2Adapter(string)
        viewPager2_timetableq.adapter = adapter
        //val tablayout = view.findViewById(R.id.tab_Layout)
        tab_Layout.tabMode = MODE_SCROLLABLE
        TabLayoutMediator(tab_Layout,viewPager2_timetableq){tab, position ->
            when(position+1){
                1-> tab.text = "Понедельник"
                2-> tab.text = "Вторник"
                3-> tab.text = "Среда"
                4-> tab.text = "Четверг"
                5-> tab.text = "Пятница"
                6-> tab.text = "Суббота"
                7-> tab.text = "Воскресенье"
            }
        }.attach()


//        val adapter = ViewPager2Adapter(string)
//        viewPager2_timetableq.adapter = adapter
        //up()

            //   Handler().postDelayed({ up() }, 1000)

//        setContentView(R.layout.activity_set_timetable)
//        val schoolName = intent.getStringExtra("schoolName")
//        val form = intent.getStringExtra("form")
//       // val number = intent.getIntExtra("nubmer")
//        Log.d("DBLog", "STActivity. form : $form, schoolName : $schoolName,  number : $number")
//        button_AddLessonToTimetable.setOnClickListener{
//            val intent = Intent(this, AddLessonToTimetableActivity::class.java)
//            intent.putExtra("schoolName", schoolName)
//            intent.putExtra("form", form)
//            //intent.putExtra("nubmer", number)
//            startActivity(intent)
//        }
//        button_Next.setOnClickListener{
//            val intent = Intent(this, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            startActivity(intent)
//        }
//
//        fetchTimetable(schoolName, form)


    }
    private fun up(){
        //val string = arrayOf<String>("dgsg", "fsdgdsfg")
        val adapter = GroupAdapter<GroupieViewHolder>()
        adapter.add(timetableDayItem())
        adapter.add(timetableDayItem())
        val view = findViewById<View>(R.id.viewPager2_timetableq)
        viewPager2_timetableq.adapter = adapter
    }

    private fun fetchTimetable(schoolName : String, form : String){
        val adapter = GroupAdapter<GroupieViewHolder>()
        val ref = FirebaseDatabase.getInstance().getReference("/schools/$schoolName/$form/timetable")
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
       // recyclerView_Timetable.adapter = adapter



    }


    class ViewPager2Adapter(val string : Array<String>) : RecyclerView.Adapter<ViewPager2Adapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2Adapter.ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.day_fragment, parent, false)
            return ViewHolder(itemView)
        }

        override fun getItemCount() = 7

        override fun onBindViewHolder(holder: ViewPager2Adapter.ViewHolder, position: Int) {
            val adapter = RecyclerViewAdapter(string)
            holder.itemView.recycleview_TimetableFragment.adapter = adapter
        }


        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    }


    class RecyclerViewAdapter(val string : Array<String>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.schools_row, parent, false)
            return ViewHolder(itemView)
        }

        override fun getItemCount() = string.size

        override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
            holder.itemView.textView_SchoolName.text = string[position]
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

    class timetableDayItem() : Item<GroupieViewHolder>(){
        override fun getLayout() = R.layout.day_fragment
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val adapter = GroupAdapter<GroupieViewHolder>()
            adapter.add(lessonItem("hello"))
            adapter.add(lessonItem("World"))
            viewHolder.itemView.recycleview_TimetableFragment.adapter = adapter
        }
    }

    class lessonItem(val lessonName : String) : Item<GroupieViewHolder>(){
        override fun getLayout() = R.layout.schools_row
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.textView_SchoolName.text = lessonName
        }
    }



}
