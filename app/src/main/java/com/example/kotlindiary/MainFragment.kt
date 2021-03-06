package com.example.kotlindiary


//import android.app.Fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlindiary.loginregister.ChooseFormActivity
import com.example.kotlindiary.loginregister.ProfileActivity
import com.example.kotlindiary.models.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.ToolBar_Main
import kotlinx.android.synthetic.main.activity_main_coordinator.*
import kotlinx.android.synthetic.main.day_fragment.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_main.*
import kotlinx.android.synthetic.main.lesson_item_card.view.*
import kotlinx.android.synthetic.main.lesson_row.view.textView
import kotlinx.android.synthetic.main.lesson_row.view.textView2
import java.util.*
import kotlin.math.abs

/**
 * A simple [Fragment] subclass.
 */


var data = Date().day-1
var previousPage = 0
lateinit var coordinatorLayoutMain : CoordinatorLayout
lateinit var bottomNavigation : BottomNavigationView
lateinit var mainviewpager : ViewPager2
lateinit var bottomsheet : BottomSheetBehavior<ConstraintLayout>
lateinit var buttonBottomSheet : Button
lateinit var buttonCopyBottomSheet : Button
lateinit var splashContainer : ConstraintLayout // сплэшскрин
lateinit var circularLoading : FrameLayout
lateinit var textSplash : TextView // для экрана загрузки
lateinit var textViewBottomSheet : TextView //дз урока
lateinit var textViewLessonBottomSheet: TextView
lateinit var editTextBottomSheet: EditText
lateinit var tabLayoutFragment : TabLayout
lateinit var listener : ValueEventListener
lateinit var listenerInside : ValueEventListener
lateinit var ListenerForSchools : ValueEventListener
lateinit var ListenerForStatus : ValueEventListener
lateinit var ref : DatabaseReference
lateinit var refTimetable : DatabaseReference
lateinit var refSchool : DatabaseReference
lateinit var refForStatus : DatabaseReference
public lateinit var userMain : User
var studentStatus = ""
var timetableDaysActivated = booleanArrayOf(false, false, false, false, false, false, false)
var year = Date().year
var month = Date().month
var date = Date().date
var day = Date().day
var currentpagedate = Calendar.getInstance()
var fff : Boolean = true

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        splashContainer = (activity as MainActivity).splashView
        //splashContainer.visibility = View.VISIBLE

        data = Date().day-1
        previousPage = 0
        timetableDaysActivated = booleanArrayOf(false, false, false, false, false, false, false)
        year = Date().year
        month = Date().month
        date = Date().date
        day = Date().day
        currentpagedate = Calendar.getInstance()
        //bottomsheet = BottomSheetBehavior.from(layoutBottomSheet)
        bottomsheet = BottomSheetBehavior.from((activity as MainActivity).layoutBottomSheet)
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
        fff = (activity as MainActivity).first
        Log.d("hey" , fff.toString())
        circularLoading = (activity as MainActivity).progress_Splash

        textSplash = (activity as MainActivity).textView_Splash
        coordinatorLayoutMain = (activity as MainActivity).coordinatorlayout
        bottomNavigation = (activity as MainActivity).bottom_navigation
        buttonBottomSheet = (activity as MainActivity).button_sheet_add
        textViewBottomSheet = (activity as MainActivity).textView_hometask
        textViewLessonBottomSheet = (activity as MainActivity).textView_lesson
        editTextBottomSheet = (activity as MainActivity).editText_hometask_bottom
        buttonCopyBottomSheet = (activity as MainActivity).button_Copy
        tabLayoutFragment = tabLayoutMainFragment
        mainviewpager = viewpager_mainfragment



        downloadHomework()

        Log.d("Timetabledownload", "==================================================== ")
        mainviewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val activity = activity as MainActivity
                changeTitle(position, activity)
                bottomsheet.state = BottomSheetBehavior.STATE_COLLAPSED

            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPause() {
        Log.d("DBlogFragment", "Pause")
        if(refSchool != null){
            refSchool.removeEventListener(ListenerForSchools)
        }
        if(ref != null){
            ref.removeEventListener(listener)
        }
        if(refTimetable != null){
            refTimetable.removeEventListener(listenerInside)
        }
        if(refForStatus != null){
            refForStatus.removeEventListener(ListenerForStatus)
        }
        super.onPause()
    }

     fun downloadHomework(){



        var calendar = Calendar.getInstance()
        var date = Date()
        Log.d("DateLog", "1: ${date.date}")
        calendar.setTime(date)
         calendar.add(Calendar.DATE, 5)
        date = calendar.getTime()
        Log.d("DateLog", "2: ${date.date}")




        val uid = FirebaseAuth.getInstance().uid
        ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        var user : User?
        listener = ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                user = p0.getValue(User::class.java)

                userMain = user!!
                val schoolName = user?.school
                val form = user?.form
                if(user?.name == "" || user?.surname == ""){
                    Log.d("whereintent", "5")
                    val intent = Intent(activity, ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    //ref.removeEventListener(listener)
                    startActivity(intent)
                    (activity as MainActivity).finish()
                }
                else if(schoolName == "" || schoolName == null){
                    val intent = Intent(activity, ChooseSchoolActivity::class.java)
                    Log.d("whereintent", "4")
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    //ref.removeEventListener(listener)
                    startActivity(intent)
                    (activity as MainActivity).finish()
                }
                else if(form == ""){
                    val intent = Intent(activity, ChooseFormActivity::class.java)
                    intent.putExtra("schoolName", schoolName)
                    Log.d("whereintent", "3")
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    //ref.removeEventListener(listener)
                    startActivity(intent)
                    (activity as MainActivity).finish()
                }
                else{
                    if(schoolName != null && form != null){
                        ifSchoolReal(schoolName, form)
                    }




                    refTimetable = FirebaseDatabase.getInstance().getReference("/schools/$schoolName/$form/timetable")

                    listenerInside = refTimetable.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {

                            var timetable = Array(7, {mutableListOf<String>()})
                            var arrayHometask = emptyArray<String>()
                            var timetableisreal = false
                            p0.children.forEach {
                                val key = it.key?.toInt()
                                if (key!=null){
                                    timetableisreal = true
                                    timetableDaysActivated[key]=true
                                    it.children.forEach{
                                        //timetable[key].add(it.toString())
                                        Log.d("Timetabledownload", "key : $key, it: ${it.toString()} ")
                                        timetable[key].add(it.value.toString())
                                    }
                                }
                            }
                            if(timetableisreal){
                                val arrayfortimetable : Array<MutableList<String>> = makeIntForTimetableAdapter()
                                if(schoolName != null && form != null){
                                    var adapter = ViewPager2Adapter(activity as MainActivity, arrayfortimetable, timetable,arrayHometask, schoolName, form){}
                                    val splashAnimation = splashContainer.animate().alpha(0F).setDuration(1000)
                                        .setUpdateListener {

                                            if(it.animatedFraction == 1f){
                                                splashContainer.visibility = View.GONE
                                                splashContainer.isEnabled = false
                                                splashContainer.elevation = 0f
                                                Log.d("dsjlksj", "yess")
                                            }
                                        }
                                    splashAnimation.start()
                                    //splashContainer.visibility = View.GONE
                                    //adapter.notifyDataSetChanged()
                                    bottomNavigation.menu.forEach { it.isEnabled = true }
                                    bottomNavigation.animate()
                                        .y((coordinatorLayoutMain.height - bottomNavigation.height).toFloat())
                                        .setDuration(500).start()
                                    refForStatus = FirebaseDatabase.getInstance().getReference("/schools/${userMain.school}/${userMain.form}/students")
                                    ListenerForStatus = refForStatus.addValueEventListener(object : ValueEventListener{
                                        override fun onDataChange(p0: DataSnapshot) {
                                            val uid = userMain.uid
                                            studentStatus = p0.child(uid).value.toString()
                                        }

                                        override fun onCancelled(p0: DatabaseError) {}
                                    })
                                    mainviewpager.adapter = adapter
                                    mainviewpager.animate().alpha(0F).setDuration(0).start()
                                    mainviewpager.setCurrentItem(250, false)
                                    mainviewpager.animate().alpha(1F).setDuration(500).start()
                                    adapterTabLayout()
                                }
                            }
                            else{
                                val intent = Intent(activity, SetTimetableActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                //ref.removeEventListener(listener)
                                //refTimetable.removeEventListener(listenerInside)
                                startActivity(intent)
                            }

                        }
                        override fun onCancelled(p0: DatabaseError) {
                            Log.d("DBLOfGFRAGMENT", "CLOSE FIRST")
                        }
                    })
                }

            }
            override fun onCancelled(p0: DatabaseError) {
                Log.d("DBLOGFRAGMENT", "CLOSE SECOND")
            }
        })

    }

    fun ifSchoolReal(schoolName : String, formName : String){
        refSchool = FirebaseDatabase.getInstance().getReference("/schools/$schoolName")
        ListenerForSchools = refSchool.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                Log.d("DBLOgggg", "${p0.child("name").getValue().toString()}")
                Log.d("DBLOgggg", p0.toString())
                if(p0.child("name").getValue()==null){
                    Log.d("whereintent", "1")
                    val intent = Intent(activity, ChooseSchoolActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
                else if(p0.child("$formName").getValue()==null){
                    Log.d("whereintent", "2")
                    val intent = Intent(activity, ChooseFormActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.putExtra("schoolName", schoolName)
                    startActivity(intent)
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        })
    }




}


class ViewPager2Adapter(val context : Context, val arrayfortimetable : Array<MutableList<String>>, val timetable : Array<MutableList<String>>, val hometask : Array<String>, val school : String, val form : String, private val itemClickListener: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.day_fragment, parent, false))

        override fun getItemCount(): Int = 500

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            for(i in 250..270){
                Log.d("LogAdapter", "i: $i, ${arrayfortimetable[i]}")
            }
            val adapter = RecyclerViewAdapter(context, arrayfortimetable[position][1],timetable[arrayfortimetable[position][0].toInt()], hometask, school, form){}
            holder.itemView.recycleview_TimetableFragment.adapter = adapter
//            val dec = DividerItemDecoration(holder.itemView.recycleview_TimetableFragment.context, DividerItemDecoration.HORIZONTAL) //Вертикальный разделитель
//            holder.itemView.recycleview_TimetableFragment.addItemDecoration(dec)
//            val dec1 = DividerItemDecoration(holder.itemView.recycleview_TimetableFragment.context, DividerItemDecoration.VERTICAL) //Горизонтальный
//            holder.itemView.recycleview_TimetableFragment.addItemDecoration(dec1)
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


class RecyclerViewAdapter(val context: Context, val date : String,val timetable : MutableList<String>, val hometask : Array<String>, val school : String, val form : String, private val itemClickListener: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val items = mutableListOf<Any>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lesson_item_card, parent, false))
            //ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lesson_row, parent, false))

        override fun getItemCount(): Int = timetable.size
//kh
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            holder.itemView.textView2.text = timetable[position]
            holder.itemView.textView3.text = (position+1).toString()
            val ref = FirebaseDatabase.getInstance().getReference("/schools/$school/$form/hometasks/$date/")
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        if(it.key == timetable[position]){
                        Log.d("forholder", "${it.key} - $position")
                            holder.itemView.textView.text = it.value.toString()
                            holder.itemView.textView.setTextColor(Color.parseColor("#444444"))
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
                textViewBottomSheet.text = ""
                textViewLessonBottomSheet.text = ""
                editTextBottomSheet.text = Editable.Factory.getInstance().newEditable("")
                Log.d("logi", "Click RecyclerView bind! Name : ${it.textView2.text}" )
                it.context.vibratePhoneClick()
                //if (bottomsheet.state == BottomSheetBehavior.STATE_COLLAPSED || bottomsheet.state == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomsheet.setState(BottomSheetBehavior.STATE_EXPANDED)
                    textViewLessonBottomSheet.text = holder.itemView.textView2.text.toString()

                    if(holder.itemView.textView.text.toString() != "нет"){
                        textViewBottomSheet.text = holder.itemView.textView.text.toString()
                        //editTextBottomSheet.text = Editable.Factory.getInstance().newEditable(textViewBottomSheet.text)
                    }
                    editTextBottomSheet.setOnKeyListener(View.OnKeyListener{v, keyCode, event -> //нажатие на энтер
                        if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP){
                            uploadHometask(holder)
                        }
                        false
                    })
                    buttonBottomSheet.setOnClickListener{
                        uploadHometask(holder)
                    }
                    buttonCopyBottomSheet.setOnClickListener {
//                        val clip =
//                            ClipData.newPlainText("", holder.itemView.textView_hometask.text.toString())
//                        context.getSystemService(Context.CLIPBOARD_SERVICE).setPrimaryClip(clip)
                    }

                //}

            }
        }
        fun uploadHometask(holder: RecyclerView.ViewHolder){
            if(studentStatus == "0"){ Toast.makeText(context, "Вы не можете изменять дз, так как Вы в бане", Toast.LENGTH_LONG).show()}
            else{
                val text = editTextBottomSheet.text
                if (text.toString() != "") {
                    bottomsheet.setState(BottomSheetBehavior.STATE_COLLAPSED)
                    Log.d("asdf", bottomsheet.state.toString())
                    var date = Date()
                    date = currentpagedate.getTime()
                    val ddate = date.date
                    val dmonth = date.month + 1
                    val dyear = date.year + 1900
                    val string = "$ddate-$dmonth-$dyear"
                    val lesson = holder.itemView.textView2.text
                    val ref = FirebaseDatabase.getInstance().getReference("/schools/$school/$form/hometasks/$string")

                    Log.d("DBLog", "string : $string, lesson : $lesson, text : $text")
                    ref.child("$lesson").setValue(text.toString())
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


private fun adapterTabLayout(){
    //var string : MutableList<String>
    var string = Array (500, {""})
    var calendarForAdapter = Calendar.getInstance()
    for(i in 250..499){
        if(timetableDaysActivated[calendarForAdapter.time.day] == false){
            while(timetableDaysActivated[calendarForAdapter.time.day]!=true){
                calendarForAdapter.add(Calendar.DATE, 1)
            }
        }
            var tmp = "${calendarForAdapter.time.day}"
            when(tmp.toInt()){
                1 -> tmp = "ПОНЕДЕЛЬНИК"
                2 -> tmp = "ВТОРНИК"
                3 -> tmp = "СРЕДА"
                4 -> tmp = "ЧЕТВЕРГ"
                5 -> tmp = "ПЯТНИЦА"
                6 -> tmp = "СУББОТА"
                0 -> tmp = "ВОСКРЕСЕНЬЕ"
            }
            string[i] = "${calendarForAdapter.time.date} $tmp"
            calendarForAdapter.add(Calendar.DATE, 1)

    }
    calendarForAdapter = Calendar.getInstance()
    calendarForAdapter.add(Calendar.DATE, -1)
    for(i in 249 downTo 0){
        if(timetableDaysActivated[calendarForAdapter.time.day] == false){
            while(timetableDaysActivated[calendarForAdapter.time.day]!=true){
                calendarForAdapter.add(Calendar.DATE, -1)
            }
        }

            var tmp = "${calendarForAdapter.time.day}"
            when(tmp.toInt()){
                1 -> tmp = "ПОНЕДЕЛЬНИК"
                2 -> tmp = "ВТОРНИК"
                3 -> tmp = "СРЕДА"
                4 -> tmp = "ЧЕТВЕРГ"
                5 -> tmp = "ПЯТНИЦА"
                6 -> tmp = "СУББОТА"
                0 -> tmp = "ВОСКРЕСЕНЬЕ"
            }
            string[i] = "${calendarForAdapter.time.date}   $tmp"
            calendarForAdapter.add(Calendar.DATE, -1)

    }
    TabLayoutMediator(tabLayoutFragment, mainviewpager){tab, position ->
        tab.text = string[position]
        Log.d("lololo", "${string[position]}, $position")
    }.attach()
}


private fun changeTitle(position: Int, activity : MainActivity){//меняем title и дату конкретного item во viewpager
    if((previousPage == 0 && position == 250)||(previousPage == position)){
        Log.d("DateLog", "UNDO pr: $previousPage, cu: $position")
        previousPage = 250
        Handler().postDelayed({mainviewpager.setCurrentItem(250, false) }, 1)
        Log.d("DateLog", "current position: ${mainviewpager.currentItem}")
        if(timetableDaysActivated[currentpagedate.time.day]==false){
            while(timetableDaysActivated[currentpagedate.time.day]!= true){
                currentpagedate.add(Calendar.DATE, 1)
            }
        }
        var string = ""
        var monthc = currentpagedate.time.month
        var datec = currentpagedate.time.date
        when(monthc){
            0 -> string = "Январь"
            1 -> string = "Февраль"
            2 -> string = "Март"
            3 -> string = "Апрель"
            4 -> string = "Май"
            5 -> string = "Июнь"
            6 -> string = "Июль"
            7 -> string = "Август"
            8 -> string = "Сентябрь"
            9 -> string = "Октябрь"
            10 -> string = "Ноябрь"
            11 -> string = "Декабрь"
        }
        Log.d("DateLog", "month: $monthc, date: $datec")
        activity.ToolBar_Main.title = string
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
            if(position-previousPage>1){//если пролистнули больше 1й страницы
                for(i in 1..(position- previousPage)){
                    currentpagedate.add(Calendar.DATE, 1)
                    if(timetableDaysActivated[currentpagedate.time.day]==false){//проверяем, существует ли день в расписании
                        while(timetableDaysActivated[currentpagedate.time.day]!= true){//если нет, +1 день
                            currentpagedate.add(Calendar.DATE, 1)
                        }
                    }
                }
            }
            else{
                currentpagedate.add(Calendar.DATE, 1)
                if(timetableDaysActivated[currentpagedate.time.day]==false){
                    while(timetableDaysActivated[currentpagedate.time.day]!= true){
                        currentpagedate.add(Calendar.DATE, 1)
                    }
                }
            }

        }
        else{
            Log.d("DateLog", "- : pr : $previousPage, cu : $position" )
            if(position- previousPage<-1){
                for(i in 1..-(position- previousPage)){
                    currentpagedate.add(Calendar.DATE, -1)
                    if(timetableDaysActivated[currentpagedate.time.day]==false){//проверяем, существует ли день в расписании
                        while(timetableDaysActivated[currentpagedate.time.day]!= true){//если нет, +1 день
                            currentpagedate.add(Calendar.DATE, -1)
                        }
                    }
                }
            }
            else{
                currentpagedate.add(Calendar.DATE, -1)
                if(timetableDaysActivated[currentpagedate.time.day] == false){
                    while(timetableDaysActivated[currentpagedate.time.day] != true){
                        currentpagedate.add(Calendar.DATE, -1)
                    }
                }
            }
        }
        previousPage = position
        dayc = currentpagedate.time.day
        monthc = currentpagedate.time.month
        datec = currentpagedate.time.date
        when(monthc){
            0 -> string = "Январь"
            1 -> string = "Февраль"
            2 -> string = "Март"
            3 -> string = "Апрель"
            4 -> string = "Май"
            5 -> string = "Июнь"
            6 -> string = "Июль"
            7 -> string = "Август"
            8 -> string = "Сентябрь"
            9 -> string = "Октябрь"
            10 -> string = "Ноябрь"
            11 -> string = "Декабрь"
        }
        Log.d("DateLog", "month: $monthc, date: $datec")
        //string = "$datec-$monthc-$dayc"
        activity.ToolBar_Main.title = string
        Log.d("DateLog", "$string")
    }
}


fun Context.vibratePhoneClick(){
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 29) {
        Log.d("vibrator","vibrator")
        //vibrator.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.EFFECT_CLICK))
        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
    } else {
        vibrator.vibrate(20)
    }
}
