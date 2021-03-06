package com.example.kotlindiary

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlindiary.loginregister.ChooseFormActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout.MODE_SCROLLABLE
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
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
import kotlinx.android.synthetic.main.layout_bottom_sheet_main.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_set_timetable.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_set_timetable.view.*
import kotlinx.android.synthetic.main.schools_row.view.*
import java.security.AccessController.getContext
import kotlin.coroutines.coroutineContext

class SetTimetableActivity : AppCompatActivity() {


var positionviewpager = 0
var array = Array(7, {mutableListOf<String>()})
var schoolName : String = ""
var form : String = ""
val dayvalues = arrayOf(1, 2, 3, 4, 5, 6, 0)
val adapter = ViewPager2Adapter(array)
var timetableDaysActivated = booleanArrayOf(false, false, false, false, false, false, false)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_timetable)
        Log.d("Checkarray", dayvalues[3].toString())
        //val adapter = ViewPager2Adapter(array)
        viewPager2_timetableq.adapter = adapter


        if(intent.getStringExtra("schoolName") == null || intent.getStringExtra("form") == null){//получения школы и класса
            val uid = FirebaseAuth.getInstance().uid //если данные из activity школы и класса пусты, то получаем из БД
            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    Log.d("DBLog", p0.child("form").getValue().toString())
                    schoolName = p0.child("school").getValue().toString()
                    form = p0.child("form").getValue().toString()
                    fetchTimetable(schoolName, form)

                    Log.d("DBLoggging", "10 ${form}, $schoolName")
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })

        }
        else{//иначе из активити
            schoolName = intent.getStringExtra("schoolName")
            form = intent.getStringExtra("form")
            fetchTimetable(schoolName, form)

            Log.d("DBLoggging", "11 ${form}, $schoolName")
        }

        var monday = emptyArray<String>()
        var tuesday = emptyArray<String>()
        var wednesday = emptyArray<String>()
        var thursday = emptyArray<String>()
        var friday = emptyArray<String>()
        var saturday = emptyArray<String>()
        var sunday = emptyArray<String>()

        val bottomsheet = BottomSheetBehavior.from(layoutBottomSheet_Timetable)

        floatac.setOnClickListener{
            Log.d("clicked", "clicked")
            if (bottomsheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomsheet.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        
        button_AddToTimetable.setOnClickListener{

            val text = textInputLayout.editText?.text

            if(text.toString() != ""){
                if(text.toString().contains(".") || text.toString().contains("#") || text.toString().contains("$") || text.toString().contains("[") || text.toString().contains("]")){
                    Toast.makeText(this, "Название урока не может содержать в себе '.', '#', '$', '[', ']'", Toast.LENGTH_SHORT).show()
                }
                else{
                    //Toast.makeText(this, textInputLayout.editText?.text, Toast.LENGTH_SHORT).show()
                    array[positionviewpager].add(text.toString())
                    Log.d("DBLoggging", "333: $positionviewpager")
                    adapter.notifyDataSetChanged()
                    bottomsheet.state = BottomSheetBehavior.STATE_COLLAPSED
                }

            }
            else{
                Toast.makeText(this, "Заполните поле", Toast.LENGTH_SHORT).show()
            }


        }


        val items = listOf("Русский язык", "Английский язык", "Алгебра", "Астрономия","Биология", "Геометрия", "География",
            "ИЗО", "Информатика", "История", "Литература", "Математика", "Музыка", "Немецкий язык", "Окружающий мир","ОБЖ", "Обществознание", "Природоведение", "Труд","Французский язык",
            "Физкультура", "Физика", "Химия", "Чтение", "Черчение")
        val adapterlist = ArrayAdapter(this, R.layout.dropdown_menu_popup_item, items)
        // (textField.editText as? AutoCompleteTextView)?.setAdapter(adapterlist)


        viewPager2_timetableq.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                positionviewpager = position
            }
        })


        layoutBottomSheet_Timetable.filled_exposed_dropdown_schools.setAdapter(adapterlist)


        bottomsheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                floatac.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start()
                //tab_Layout.animate().y(((0-appbarlayout.height).toFloat())*slideOffset).setDuration(0).start()
//                if(slideOffset == 1.toFloat()){
//                    floatac.animate().scaleX(0.toFloat()).scaleY(0.toFloat()).setDuration(300).start()
//                    floatac.setImageResource(R.drawable.ic_add_black_36dp)
//                    floatac.animate().scaleX(1.toFloat()).scaleY(1.toFloat()).setDuration(300).start()
//                }
//                else {
//                    floatac.animate().scaleX(0.toFloat()).scaleY(0.toFloat()).setDuration(300).start()
//                    floatac.setImageResource(R.drawable.ic_send_black_24dp)
//                    floatac.animate().scaleX(1.toFloat()).scaleY(1.toFloat()).setDuration(300).start()
//                }
                if(slideOffset != 1.toFloat()){
//                    val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.hideSoftInputFromWindow(appbarlayout.windowToken, 0)
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val yx = tab_Layout.y
                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    //floatac.animate().scaleX(0.toFloat()).scaleY(0.toFloat()).setDuration(100).start()
                    viewPager2_timetableq.isUserInputEnabled = false

                    floatac.isClickable = false
                        //tab_Layout.isEnabled =false

//                    floatac.animate().scaleX(0.toFloat()).scaleY(0.toFloat()).setDuration(100)
//                        .start()
//                    Handler().postDelayed(
//                        { floatac.setImageResource(R.drawable.ic_add_black_36dp) },
//                        100
//                    )
//
//                    floatac.animate().scaleX(1.toFloat()).scaleY(1.toFloat()).setDuration(100)
//                        .start()
                }
                else{
                    //Handler().postDelayed({ val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager //TODO: uncomment
                    //    imm.hideSoftInputFromWindow(appbarlayout.windowToken, 0) }, 100)
//                    val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.hideSoftInputFromWindow(coordinatorlayout_SetTimetable.windowToken, 0)
                    viewPager2_timetableq.isUserInputEnabled = true
                    floatac.isClickable = true
                    //tab_Layout.animate().y(appbarlayout.height.toFloat()).setDuration(100).start()
                }
            }
        })
        val itemnext = findViewById<View>(R.id.menu_next)
            itemnext.setOnClickListener {
                Log.d("DBLog", "Clicked inside listener YAY")
                uploadTimetable(array)
            }
//        val string = arrayOf<String>("dgsg", "fsdgdsfg")
//        val adapter = ViewPager2Adapter(string)
//        viewPager2_timetableq.adapter = adapter
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

    override fun onBackPressed() {
        val intent = Intent(this, ChooseFormActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("schoolName", schoolName)
        Log.d("DBLoggging", "Give school to chooseform, $schoolName")
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.reg_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_next -> {
                //uploadTimetable(array)
                Log.d("DBLog", "Clicked menu")
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun uploadTimetable(timetable : Array<MutableList<String>>){
//        for(i in 0..6){
//            for(k in 0..(array[i].size-1)){
//                if(array[dayvalues[i]][k] != null){
//                    //Log.d("timetablelog", "${array[dayvalues[i]][k]} ${dayvalues[i]} $k")
//                }
//
//            }
//        }
        array.forEach {
            Log.d("timetablelog", "${it.toString()} ${array.indexOf(it)}")
        }

        val ref = FirebaseDatabase.getInstance().getReference("/schools/$schoolName/$form/timetable")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                var arrayDB = Array(7, {mutableListOf<String>()})
                array.forEachIndexed { index, mutableList ->
                    val indexOut = index

                    p0.child("${dayvalues[indexOut]}").children.forEach {
                        arrayDB[index].add(it.getValue().toString())
                        Log.d("asdgg", it.getValue().toString())
                    }
                    Log.d("asdgg", arrayDB[indexOut].toString())
                    Log.d("timetableloga", p0.child("${dayvalues[indexOut]}").toString())

                    if(array[index].size >= arrayDB[index].size){
                        array[index].forEachIndexed { indexIn, s ->
                            if((indexIn > arrayDB[index].size - 1) || (s != arrayDB[index][indexIn])){
                                ref.child("${dayvalues[index]}").child("$indexIn").setValue(s)
                            }
                        }
                    }
                    else{
                        arrayDB[index].forEachIndexed { indexIn, s ->
                            if((indexIn <= array[index].size -1) && (s != array[index][indexIn])){
                                ref.child("${dayvalues[index]}").child("$indexIn").setValue(array[index][indexIn])
                            }
                            else if(indexIn > array[index].size - 1){
                                ref.child("${dayvalues[index]}").child("$indexIn").removeValue()
                            }
                        }
                    }

                }


            }
            override fun onCancelled(p0: DatabaseError) {}
        })

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun fetchTimetable(schoolNameIn : String, formIn : String){
        val ref = FirebaseDatabase.getInstance().getReference("/schools/$schoolNameIn/$formIn/timetable")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d("DBLoggging", it.key)
                    var keyPos = it.key?.toInt()
                    if(keyPos!=null) {
                        it.children.forEach {
                            Log.d("DBLoggging", it.value.toString())
                            if(keyPos == 0){array[6].add(it.value.toString())}
                            else{array[keyPos-1].add(it.value.toString())}
                        }
                    }
                }
                viewPager2_timetableq.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    class ViewPager2Adapter(val string : Array<MutableList<String>>) : RecyclerView.Adapter<ViewPager2Adapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2Adapter.ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.day_fragment, parent, false)
            return ViewHolder(itemView)
        }

        override fun getItemCount() = 7

        override fun onBindViewHolder(holder: ViewPager2Adapter.ViewHolder, position: Int) {
            val adapter = RecyclerViewAdapter(string[position]){}
            holder.itemView.recycleview_TimetableFragment.adapter = adapter
            holder.itemView.recycleview_TimetableFragment.setOnClickListener {
                Log.d("loggggi", "clicked")
            }
            val dec = DividerItemDecoration(holder.itemView.recycleview_TimetableFragment.context, DividerItemDecoration.HORIZONTAL) //Вертикальный разделитель
            holder.itemView.recycleview_TimetableFragment.addItemDecoration(dec)
            val dec1 = DividerItemDecoration(holder.itemView.recycleview_TimetableFragment.context, DividerItemDecoration.VERTICAL) //Горизонтальный
            holder.itemView.recycleview_TimetableFragment.addItemDecoration(dec1)
            //holder.itemView.recycleview_TimetableFragment.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        }


        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        }


    }




    class RecyclerViewAdapter(val string : MutableList<String>, private val itemClickListener: (Int) -> Unit) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.schools_row, parent, false)
            return ViewHolder(itemView)
        }

        override fun getItemCount() = string.size

        override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
            holder.itemView.textView_SchoolName.text = string[position]
            holder.itemView.setOnLongClickListener(object : View.OnLongClickListener{
                override fun onLongClick(v: View?): Boolean {
                    Log.d("logi", "Inside bind long click at ${holder.itemView.textView_SchoolName.text}")
                    val builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Удалить урок?")
                    builder.setMessage("Вы хотите удалить урок из списка?")
                    builder.setPositiveButton("Да"){dialog, which ->
                        //Toast.makeText(holder.itemView.context, "Удалено", Toast.LENGTH_SHORT).show()
                        string.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, string.size)
                    }
                    builder.setNegativeButton("Нет"){dialog, which ->
                    }
                    builder.create().show()
                    return true
                }
            })



        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            init {
                itemView.setOnClickListener {
                    //Log.d("logi", "Click RecyclerView!") // WORKS!!!
                    itemClickListener(adapterPosition)
                }
                itemView.setOnLongClickListener(object : View.OnLongClickListener{
                    override fun onLongClick(v: View?): Boolean {

                        Log.d("logi", "Long click at ${itemView.textView_SchoolName.text}")
                        return true
                    }
                })
            }
        }
    }

}
