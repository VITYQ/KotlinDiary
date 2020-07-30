package com.example.kotlindiary


//import android.app.Fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.size
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlindiary.models.User
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener
import kotlinx.android.synthetic.main.activity_add_lesson.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.ToolBar_Main
import kotlinx.android.synthetic.main.activity_main_coordinator.*
import kotlinx.android.synthetic.main.activity_set_timetable.*
import kotlinx.android.synthetic.main.activity_set_timetable.view.*
import kotlinx.android.synthetic.main.day_fragment.*
import kotlinx.android.synthetic.main.day_fragment.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_main.*
import kotlinx.android.synthetic.main.lesson_row.view.*
import java.security.AccessController.getContext
import java.util.*

/**
 * A simple [Fragment] subclass.
 */


var data = Date().day-1
var previousPage = 0
lateinit var mainviewpager : ViewPager2
lateinit var bottomsheet : BottomSheetBehavior<ConstraintLayout>
lateinit var buttonBottomSheet : Button
lateinit var editTextBottomSheet: EditText
var timetableDaysActivated = booleanArrayOf(false, false, false, false, false, false, false)
var year = Date().year
var month = Date().month
var date = Date().date
var day = Date().day
var currentpagedate = Calendar.getInstance()

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        data = Date().day-1
        previousPage = 0
        timetableDaysActivated = booleanArrayOf(false, false, false, false, false, false, false)
        year = Date().year
        month = Date().month
        date = Date().date
        day = Date().day
        currentpagedate = Calendar.getInstance()
        //bottomsheet = BottomSheetBehavior.from(layoutBottomSheet)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("datelogging", "Today is ${currentpagedate.time.day.toString()}")
        Log.d("Datelog", data.toString())
        for(i in 0..timetableDaysActivated.size-1){
            Log.d("DBLog", "BEFORE: i : $i, ${timetableDaysActivated[i].toString()}")
        }
        Log.d("Datelog", currentpagedate.time.day.toString())
        bottomsheet = BottomSheetBehavior.from((activity as MainActivity).layoutBottomSheet)
        (activity as MainActivity).ToolBar_Main.title = "$date $month, $day"
        buttonBottomSheet = (activity as MainActivity).button_sheet_add
        editTextBottomSheet = (activity as MainActivity).editText_hometask_bottom
        mainviewpager = viewpager_mainfragment
        downloadHomework()
        Log.d("Timetabledownload", "==================================================== ")
        mainviewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                Log.d("DBlog", position.toString())
                super.onPageSelected(position)
                val activity = activity as MainActivity
                Log.d("ChangeTitle", "ChangeCallback: $position")
                changeTitle(position, activity)
                bottomsheet.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        })
    }

    override fun onResume() {
        Log.d("checkforresume", "started")
        super.onResume()
    }
}




public fun downloadHomework(){
    var calendar = Calendar.getInstance()
    var date = Date()
    Log.d("DateLog", "1: ${date.date}")
    calendar.setTime(date)
    calendar.add(Calendar.DATE, 5)
    date = calendar.getTime()
    Log.d("DateLog", "2: ${date.date}")
    val uid = FirebaseAuth.getInstance().uid
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
    var user : User?


    ref.addValueEventListener(object : ValueEventListener{
        override fun onDataChange(p0: DataSnapshot) {
            user = p0.getValue(User::class.java)
            val schoolName = user?.school
            val form = user?.form
            val refTimetable = FirebaseDatabase.getInstance().getReference("/schools/$schoolName/$form/timetable")
            refTimetable.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    var timetable = Array(7, {mutableListOf<String>()})
                    var arrayHometask = emptyArray<String>()
                    p0.children.forEach {
                        val key = it.key?.toInt()
                        if (key!=null){
                            timetableDaysActivated[key]=true
                            it.children.forEach{
                                //timetable[key].add(it.toString())
                                Log.d("Timetabledownload", "key : $key, it: ${it.toString()} ")
                                timetable[key].add(it.value.toString())
                            }
                        }
//
                    }
                    val arrayfortimetable : Array<MutableList<String>> = makeIntForTimetableAdapter()
                    if(schoolName != null && form != null){
                        var adapter = ViewPager2Adapter(arrayfortimetable, timetable,arrayHometask, schoolName, form){}
                        //adapter.notifyDataSetChanged()
                        mainviewpager.adapter = adapter
                    }
                    for(i in 0..6){
                        timetable[i].forEach{
                            Log.d("DBLog", "Day $i : ${it}")
                        }
                    }
                    for(i in 0..timetableDaysActivated.size-1){
                        Log.d("DBLog", "AFTER: i : $i, ${timetableDaysActivated[i].toString()}")
                    }
                }
                override fun onCancelled(p0: DatabaseError) {}
            })
        }
        override fun onCancelled(p0: DatabaseError) {}
    })
}


class ViewPager2Adapter(val arrayfortimetable : Array<MutableList<String>>, val timetable : Array<MutableList<String>>, val hometask : Array<String>, val school : String, val form : String, private val itemClickListener: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val items = mutableListOf<Any>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.day_fragment, parent, false))

        override fun getItemCount(): Int = 500

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


            for(i in 250..270){
                Log.d("LogAdapter", "i: $i, ${arrayfortimetable[i]}")
            }






            val adapter = RecyclerViewAdapter(arrayfortimetable[position][1],timetable[arrayfortimetable[position][0].toInt()], hometask, school, form){}
            holder.itemView.recycleview_TimetableFragment.adapter = adapter
            //holder.itemView.recycleview_TimetableFragment.addItemDecoration(DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL))
//            holder.itemView.recycleview_TimetableFragment.setOnClickListener {
//                Log.d("logi", "Click inside bind")
//            }
            if(position == 1){
                Log.d("creater","yup")

            }
            Log.d("created", "Created")
        }

        private inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

            init {
                itemView.setOnClickListener {
                    Log.d("logi", "Click ViewPager!") // WORKS!!!
                    itemClickListener(adapterPosition)
                }
            }
        }
    }


class RecyclerViewAdapter(val date : String,val timetable : MutableList<String>, val hometask : Array<String>, val school : String, val form : String, private val itemClickListener: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val items = mutableListOf<Any>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lesson_row, parent, false))

        override fun getItemCount(): Int = timetable.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.textView2.text = timetable[position]
            val ref = FirebaseDatabase.getInstance().getReference("/schools/$school/$form/hometasks/$date/")
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        if(it.key == timetable[position]){
                        //Log.d("DBLogging", "key: $")
                            holder.itemView.textView.text = it.value.toString()
                        }

                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
            //Log.d("DBLog", "Hometask : ${hometask[position]}")
//            if (hometask[position] == "null"){
//                holder.itemView.textView.text = "Нажмите, чтобы добавить задание"
//            }
//            else{
//                holder.itemView.textView.text = hometask[position]
//            }
            holder.itemView.setOnClickListener {
                Log.d("logi", "Click RecyclerView bind! Name : ${it.textView2.text}" )
                val view = it
                if (bottomsheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomsheet.state = BottomSheetBehavior.STATE_EXPANDED
                    buttonBottomSheet.setOnClickListener{
                        bottomsheet.state = BottomSheetBehavior.STATE_COLLAPSED
                        Log.d("DBLog", "clicked bottom sheet button at ${view.textView2.text}")
                        //mainviewpager.setCurrentItem(250)
                        var date = Date()
                        date = currentpagedate.getTime()
                        val ddate = date.date
                        val dmonth = date.month + 1
                        val dyear = date.year + 1900
                        val string = "$ddate-$dmonth-$dyear"
                        val lesson = holder.itemView.textView2.text
                        val text = editTextBottomSheet.text
                        val ref = FirebaseDatabase.getInstance().getReference("/schools/$school/$form/hometasks/$string")

                        Log.d("DBLog", "string : $string, lesson : $lesson, text : $text")
                        ref.child("$lesson").setValue(text.toString())
                    }

                }
                else {
                    bottomsheet.state = BottomSheetBehavior.STATE_COLLAPSED

                }
            }
        }

        private inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

            init {
                itemView.setOnClickListener {
                    Log.d("logi", "Click RecyclerView!") // WORKS!!!
                    itemClickListener(adapterPosition)
                }
            }
        }
    }


fun makeIntForTimetableAdapter() : Array<MutableList<String>>{
        //var arrayint = Array(500, {0})
        var arrayint = Array(500, { mutableListOf<String>()})
        var calendarforadapter = Calendar.getInstance()
        for(i in 250..499){
            if(timetableDaysActivated[calendarforadapter.time.day] == false){
                while(timetableDaysActivated[calendarforadapter.time.day]!=true){
                    calendarforadapter.add(Calendar.DATE, 1)
                }
            }
            arrayint[i].add(calendarforadapter.time.day.toString())
            val date = calendarforadapter.time.date
            val month = calendarforadapter.time.month+1
            val year = calendarforadapter.time.year+1900
            arrayint[i].add("$date-$month-$year")
            calendarforadapter.add(Calendar.DATE, 1)
        }
        calendarforadapter = Calendar.getInstance()
        calendarforadapter.add(Calendar.DATE, -1)
        for(i in 249 downTo 0){
            if(timetableDaysActivated[calendarforadapter.time.day] == false){
                while(timetableDaysActivated[calendarforadapter.time.day]!=true){
                    calendarforadapter.add(Calendar.DATE, -1)
                }
            }
            arrayint[i].add(calendarforadapter.time.day.toString())
            val date = calendarforadapter.time.date
            val month = calendarforadapter.time.month+1
            val year = calendarforadapter.time.year+1900
            arrayint[i].add("$date-$month-$year")
            calendarforadapter.add(Calendar.DATE, -1)
        }
        return arrayint
    }


private fun changeTitle(position: Int, activity : MainActivity){
    if((previousPage == 0 && position == 250)||(previousPage == position)){
        Log.d("DateLog", "UNDO pr: $previousPage, cu: $position")
        previousPage = 250
        Handler().postDelayed({mainviewpager.setCurrentItem(250, false) }, 1)
    }
    else {
        Log.d("DateLog", "pr: $previousPage, cu: $position")

        var monthc = 0
        var datec = 0
        var string = ""
        var date = Date()
        date = currentpagedate.getTime()
        var dayc = date.day-1

        if(position > previousPage){
            Log.d("DateLog", "+")
            currentpagedate.add(Calendar.DATE, 1)
            if(timetableDaysActivated[currentpagedate.time.day]==false){
                while(timetableDaysActivated[currentpagedate.time.day]!= true){
                    currentpagedate.add(Calendar.DATE, 1)
                }
            }
        }
        else{
            Log.d("DateLog", "- : pr : $previousPage, cu : $position" )
            currentpagedate.add(Calendar.DATE, -1)
            if(timetableDaysActivated[currentpagedate.time.day] == false){
                while(timetableDaysActivated[currentpagedate.time.day] != true){
                    currentpagedate.add(Calendar.DATE, -1)
                }
            }
        }
        previousPage = position
        dayc = currentpagedate.time.day
        monthc = currentpagedate.time.month
        datec = currentpagedate.time.date
        string = "$datec $monthc, $dayc"
        activity.ToolBar_Main.title = string
        Log.d("DateLog", "$string")
    }
}
