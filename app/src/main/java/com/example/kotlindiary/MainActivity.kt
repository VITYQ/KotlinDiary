package com.example.kotlindiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlindiary.loginregister.RegisterActivity
import com.example.kotlindiary.models.User
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.ToolBar_Main
import kotlinx.android.synthetic.main.activity_main.viewpager_main
import kotlinx.android.synthetic.main.activity_main_coordinator.*
import kotlinx.android.synthetic.main.add_hometask_fragment.*
import kotlinx.android.synthetic.main.day_fragment.*
import kotlinx.android.synthetic.main.day_fragment.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_main.*
import kotlinx.android.synthetic.main.lesson_row.view.*
import java.time.LocalDate
import java.util.*

class MainActivity : AppCompatActivity() {

// TODO: Решить баг с вылетанием после частого переключения на MainFragment
    lateinit var mainFragment: MainFragment

    val a = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_coordinator)
        val bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)



        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, MainFragment())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

//        viewpager_mainfragment.setOnClickListener {
//            Log.d("DBLog", "Clicked MainActivity")
//        }

//        recycleview_TimetableFragment.setOnClickListener(object : View.OnClickListener{
//            override fun onClick(v: View?) {
//                Log.d("LogListener", "Clicked : $v")
//            }
//        })
//        val bottomSheetBehavior = BottomSheetBehavior.from(addhometaskfragment)
//        fab.setOnClickListener(){
////            val intent = Intent(this, AddLessonActivity::class.java)
////
////            startActivity(intent)
//            if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED){bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED}
//            else{bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED}
//        }
        setSupportActionBar(findViewById(R.id.ToolBar_Main))
        //ToolBar.setTitle("Расписание2")
        //ToolBar_Main.title = "Расписание1"
        ToolBar_Main.title = "Заголовок"
        //fetchHomework()
        //verifyIsUserLoggedIn()
        //fetchHomework()
//        val string = arrayOf<String>("dgsg", "fsdgdsfg")
//        val adapter = GroupAdapter<GroupieViewHolder>()
//        adapter.add(DayItem(string))
//        mainFragment.viewpager_main.adapter = adapter
        //supportActionBar?.title = "Расписание"
        val bar = actionBar
        bar?.title = "Расписание3"
        var date = Date().time
        date = date + 100000000
        val year = Date(date).date

        Log.d("Date", "year: ${Date()}")
        //val adapter = GroupAdapter<GroupieViewHolder>()
        //recycleview_timetable.adapter = adapter




//        val vPadapter = GroupAdapter<GroupieViewHolder>()
//        for (i in 1..501){
//            vPadapter.add(DayItem())
////        }
//        viewpager_main.adapter = vPadapter
//        viewpager_main.setCurrentItem(251)

//        fab.setOnClickListener(){
//            vPadapter.add(DayItem())
//        }
        //Date createdAt = new Date()
//        var data = Date().day
//        //Log.d("Date", "$createdAt")
//        val previousPage = 251
//        when (data) {
//            1 -> ToolBar_Main.title = "Пн."
//            2 -> ToolBar_Main.title = "Вт."
//            3 -> ToolBar_Main.title = "Ср."
//            4 -> ToolBar_Main.title = "Чт."
//            5 -> ToolBar_Main.title = "Пт."
//            6 -> ToolBar_Main.title = "Сб."
//            7 -> ToolBar_Main.title = "Вс."
//        }
//        viewpager_main.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback){
//            override fun onPageSelected(position: Int){
//                super .onPage
//            }
//        }
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                bottom_navigation.animate().y((coordinatorlayout.height-bottom_navigation.height+bottom_navigation.height*slideOffset).toFloat()).setDuration(0).start()
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
        })
        bottom_navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener{
            when(it.itemId){
                bottom_navigation.selectedItemId -> {return@OnNavigationItemSelectedListener true}
                R.id.one -> {

                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, MainFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.three -> {
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                       // bottom_navigation.visibility = View.GONE
                        //bottom_navigation.animate().scaleX(0.toFloat()).scaleY(0.toFloat()).setDuration(1000).start()
                        //bottom_navigation.animate().y((bottom_navigation.height+coordinatorlayout.height).toFloat()).setDuration(1000).start()
                    }
                    else {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        //bottom_navigation.visibility = View.VISIBLE
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.two -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, SettingsFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })



//        mainFragment.viewpager_mainfragment.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
//            override fun onPageSelected(position: Int) {
//                Log.d("DBlog", position.toString())
//                super.onPageSelected(position)
//                if (position > previousPage){
//                    if(data == 7) data = 1
//                    else data++
//                }
//                else{
//                    if(data == 1) data = 7
//                    else data--
//                }
//                when (data) {
//                    1 -> ToolBar_Main.title = "Пн."
//                    2 -> ToolBar_Main.title = "Вт."
//                    3 -> ToolBar_Main.title = "Ср."
//                    4 -> ToolBar_Main.title = "Чт."
//                    5 -> ToolBar_Main.title = "Пт."
//                    6 -> ToolBar_Main.title = "Сб."
//                    7 -> ToolBar_Main.title = "Вс."
//                }
//            }
//        })
//        viewpager_mainfragment.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
//            override fun onPageSelected(position: Int) {
//                Log.d("DBlog", position.toString())
//                super.onPageSelected(position)
//                if (position > previousPage){
//                    if(data == 7) data = 1
//                    else data++
//                }
//                else{
//                    if(data == 1) data = 7
//                    else data--
//                }
//                when (data) {
//                    1 -> ToolBar_Main.title = "Пн."
//                    2 -> ToolBar_Main.title = "Вт."
//                    3 -> ToolBar_Main.title = "Ср."
//                    4 -> ToolBar_Main.title = "Чт."
//                    5 -> ToolBar_Main.title = "Пт."
//                    6 -> ToolBar_Main.title = "Сб."
//                    7 -> ToolBar_Main.title = "Вс."
//                }
//            }
//        })
//
        //fetchHomework()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            R.id.menu_add ->{

            }
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


//    private fun fetchHomework(){
//        val uid = FirebaseAuth.getInstance().uid
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//        var user : User?
//        ref.addListenerForSingleValueEvent(object : ValueEventListener{
//            override fun onDataChange(p0: DataSnapshot) {
//                user = p0.getValue(User::class.java)
//                Log.d("DBlog", "${user?.school.toString()}, ${user?.form.toString()}")
//                val schoolName = user?.school
//                val form = user?.form
//                var array = emptyArray<String>()
//                val adapter = GroupAdapter<GroupieViewHolder>()
//                val refTimetable = FirebaseDatabase.getInstance().getReference("/schools/$schoolName/$form/timetable")
//                refTimetable.addListenerForSingleValueEvent(object : ValueEventListener{
//                    override fun onDataChange(p0: DataSnapshot) {
//                        p0.children.forEach{
//                            array += it.child("lessonName").getValue().toString()
//                        }
//                        for(i in 1..501){
//                            adapter.add(DayItem(array))
//                        }
//                        mainFragment.viewpager_main.setCurrentItem(251)
//
//                        Log.d("DBlog", "${viewpager_main.currentItem.toString()}")
//
//
//                    }
//
//                    override fun onCancelled(p0: DatabaseError) {
//
//                    }
//                })
//
//                mainFragment.viewpager_main.adapter = adapter
//
//
//
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//        })
//    }



//    private fun fetchHomework(){
//        val uid = FirebaseAuth.getInstance().uid
//        val refschool = FirebaseDatabase.getInstance().getReference("/users/$uid")
//            .addListenerForSingleValueEvent(object : ValueEventListener{
//                override fun onDataChange(p0: DataSnapshot) {
//                    val schoolName = p0.getValue(User::class.java)?.school.toString()
//                    Log.d("FireBaseLogging", "schoolName: $schoolName")
//
//
//
//
//                    val ref = FirebaseDatabase.getInstance().getReference("/schools/$schoolName/hometasks")
//                    val adapter = GroupAdapter<GroupieViewHolder>()
//                    ref.addListenerForSingleValueEvent(object : ValueEventListener{
//                        override fun onDataChange(p0: DataSnapshot) {
//                            p0.children.forEach{
//                                val hometaskitem = it.getValue(AddLessonActivity.hometaskitem::class.java)
//                                if (hometaskitem != null){
//                                    adapter.add(LessonItem(hometaskitem))
//                                }
//                            }
//
//                        }
//
//                        override fun onCancelled(p0: DatabaseError) {
//
//                        }
//                    })
//                    recycleview_timetable.adapter = adapter
//                }
//
//                override fun onCancelled(p0: DatabaseError) {
//
//                }
//            })



    }

//    class DayItem(val timetable : Array<String>):Item<GroupieViewHolder>(){
//        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//            //viewHolder.itemView.textView3.text = position.toString()
//            val adapter = GroupAdapter<GroupieViewHolder>()
//            //val lol = AddLessonActivity.hometaskitem("Заголовок","Контент","cheburek")
//            timetable.forEach {
//                adapter.add(LessonItem(it.toString()))
//            }
//            viewHolder.itemView.recycleview_TimetableFragment.adapter=adapter
//            adapter.setOnItemClickListener{item, view ->
//
//            }
//
//        }
//
//        override fun getLayout(): Int {
//            return R.layout.day_fragment
//        }
//
//       // override fun getItemCount(): Int = 1
//    }



//
//    class LessonItem(val lesson : String): Item<GroupieViewHolder>(){
//        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//            Log.d("FireBaseLogging", "ONE")
//            viewHolder.itemView.textView2.text = lesson
//            //viewHolder.itemView.textView.text = hometaskitem.hometask.toString()
//        }
//
//        override fun getLayout(): Int {
//            Log.d("FireBaseLogging", "TWO")
//            return R.layout.lesson_row
//        }
//    }


//
//    private fun verifyIsUserLoggedIn(){
//        val uid = FirebaseAuth.getInstance().uid
//        if (uid == null){
//            val intent = Intent(this , RegisterActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }
//    }
//}
