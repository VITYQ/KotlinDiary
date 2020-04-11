package com.example.kotlindiary


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
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
import kotlinx.android.synthetic.main.activity_set_timetable.*
import kotlinx.android.synthetic.main.activity_set_timetable.view.*
import kotlinx.android.synthetic.main.day_fragment.*
import kotlinx.android.synthetic.main.day_fragment.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_main.*
import kotlinx.android.synthetic.main.lesson_row.view.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */


var data = Date().day
var previousPage = 251
lateinit var mainviewpager : ViewPager2
lateinit var bottomsheet : BottomSheetBehavior<ConstraintLayout>
lateinit var buttonBottomSheet : Button


class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //bottomsheet = BottomSheetBehavior.from(layoutBottomSheet)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val string = arrayOf<String>("dgsg", "fsdgdsfg")
        bottomsheet = BottomSheetBehavior.from((activity as MainActivity).layoutBottomSheet)
        //bottomsheet.state = BottomSheetBehavior.STATE_EXPANDED
//        val adapter = GroupAdapter<GroupieViewHolder>()
//        for(i in 1..501){
//            adapter.add(DayItem(string))
//        }
//
//        viewpager_mainfragment.adapter = adapter
//        viewpager_mainfragment.setCurrentItem(251)
//        fetchHomework()
//        //MainActivity.ToolBar_Main.title = "АГА РАБОТАЕТ"
//        (activity as MainActivity).ToolBar_Main.title = "fff"

//        var data = Date().day
//        //Log.d("Date", "$createdAt")
//        var previousPage = 251
        buttonBottomSheet = (activity as MainActivity).button_sheet_add
//        (activity as MainActivity).button_sheet_add.setOnClickListener{
//            Log.d("DBLog", "Clicked bottom sheet button")
//        }



        when (data) {
            1 -> (activity as MainActivity).ToolBar_Main.title = "Пн."
            2 -> (activity as MainActivity).ToolBar_Main.title = "Вт."
            3 -> (activity as MainActivity).ToolBar_Main.title = "Ср."
            4 -> (activity as MainActivity).ToolBar_Main.title = "Чт."
            5 -> (activity as MainActivity).ToolBar_Main.title = "Пт."
            6 -> (activity as MainActivity).ToolBar_Main.title = "Сб."
            7 -> (activity as MainActivity).ToolBar_Main.title = "Вс."
        }

        //fetchHomework()

        mainviewpager = viewpager_mainfragment

//        mainviewpager.adapter = ViewPager2Adapter(string) {
//            Log.d("logi", "clicked at : $it")
//        }



        downloadHomework()


        mainviewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                Log.d("DBlog", position.toString())
                super.onPageSelected(position)
                //changeTitle(position)

                recycleview_TimetableFragment.setOnClickListener{
                    Log.d("logi", "Click inside oncreated")
                }


            }
        })


        }
//        viewpager_mainfragment.recyclerView_Timetable.setOnClickListener {
//            Log.d("ClickListenerMain", "clicked : $view")
//        }
//        recycleview_TimetableFragment.setOnClickListener {
//            Log.d("ClickListenerMain", "clicked : $view")
//        }


    }



public fun test(){
    val string = arrayOf<String>("dgsg", "fsdgdsfg")
    mainviewpager.adapter = ViewPager2Adapter(string, string){}
}


public fun downloadHomework(){
    val uid = FirebaseAuth.getInstance().uid
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
    var user : User?
    var array = emptyArray<String>()
    var arrayHometask = emptyArray<String>()
    ref.addListenerForSingleValueEvent(object : ValueEventListener{
        override fun onDataChange(p0: DataSnapshot) {
            user = p0.getValue(User::class.java)
            val schoolName = user?.school
            val form = user?.form

            val refTimetable = FirebaseDatabase.getInstance().getReference("/schools/$schoolName/$form/timetable")
            refTimetable.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach{

                        array += it.child("lessonName").getValue().toString()
                        arrayHometask += it.child("hometask").getValue().toString()
                        //Log.d("DBLog", "${array[0]}")

                    }
                    array.forEach {
                        Log.d("DBLog", "Inside ondatachange: $it")
                    }
                    mainviewpager.adapter = ViewPager2Adapter(array,arrayHometask){}
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })


        }

        override fun onCancelled(p0: DatabaseError) {

        }


    })

//    val string = arrayOf<String>("dgsg", "fsdgdsfg")
//    array.forEach {
//        Log.d("DBLog", it)
//    }
//    mainviewpager.adapter = ViewPager2Adapter(array){}

}





    class ViewPager2Adapter(val timetable : Array<String>, val hometask : Array<String>, private val itemClickListener: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val items = mutableListOf<Any>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.day_fragment, parent, false))

        override fun getItemCount(): Int = 500

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val adapter = RecyclerViewAdapter(timetable, hometask){}
            holder.itemView.recycleview_TimetableFragment.adapter = adapter
//            holder.itemView.recycleview_TimetableFragment.setOnClickListener {
//                Log.d("logi", "Click inside bind")
//            }
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


    class RecyclerViewAdapter(val timetable: Array<String>, val hometask : Array<String>, private val itemClickListener: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val items = mutableListOf<Any>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lesson_row, parent, false))

        override fun getItemCount(): Int = timetable.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.textView2.text = timetable[position]
            Log.d("DBLog", "Hometask : ${hometask[position]}")
            if (hometask[position] == "null"){
                holder.itemView.textView.text = "Нажмите, чтобы добавить задание"
            }
            else{
                holder.itemView.textView.text = hometask[position]
            }
            holder.itemView.setOnClickListener {
                Log.d("logi", "Click RecyclerView bind! Name : ${it.textView2.text}" )
                val view = it
                if (bottomsheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomsheet.state = BottomSheetBehavior.STATE_EXPANDED
                    buttonBottomSheet.setOnClickListener{
                        Log.d("DBLog", "clicked bottom sheet button at ${view.textView2.text}")
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




//    private fun downloadHomework(){
//        val uid = FirebaseAuth.getInstance().uid
//        Log.d("Loggingg", "The uid : $uid")
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//        var user : User?
//        ref.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(p0: DataSnapshot) {
//                user = p0.getValue(User::class.java)
//                Log.d("DBlog", "${user?.school.toString()}, ${user?.form.toString()}")
//                val schoolName = user?.school
//                val form = user?.form
//                var array = emptyArray<String>()
//                val adapter = GroupAdapter<GroupieViewHolder>()
//                val refTimetable = FirebaseDatabase.getInstance().getReference("/schools/$schoolName/$form/timetable")
//
//                refTimetable.addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(p0: DataSnapshot) {
//                        p0.children.forEach{
//                            array += it.child("lessonName").getValue().toString()
//                        }
//                        for(i in 1..501){
//                            adapter.add(com.example.kotlindiary.DayItem(array))
//                        }
//                        viewpager_mainfragment.setCurrentItem(251)
//
//                        Log.d("DBlog", "${viewpager_mainfragment.currentItem.toString()}")
//
//
//                    }
//
//                    override fun onCancelled(p0: DatabaseError) {
//
//                    }
//                })
//
//               viewpager_mainfragment.adapter = adapter
//
//
//
//
//                //adapter.setOnItemClickListener(OnItemClickListener { item, view ->  })
//
////                    .setOnItemClickListener(object : View.OnClickListener(){
////                        override fun onClick(v: View?) {
////
////                        }
////                    })
//
//
////                adapter.setOnItemClickListener{item, view ->
////                    Log.d("LogListener", "clicked on $item")
////                }
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




//
//
//    private fun changeTitle(position: Int){
//        if (position > previousPage){
//            if(data == 7) data = 1
//            else data++
//            previousPage++
//        }
//        else{
//            if(data == 1) data = 7
//            else data--
//            previousPage--
//        }
//        when (data) {
//            1 -> (activity as MainActivity).ToolBar_Main.title = "Пн."
//            2 -> (activity as MainActivity).ToolBar_Main.title = "Вт."
//            3 -> (activity as MainActivity).ToolBar_Main.title = "Ср."
//            4 -> (activity as MainActivity).ToolBar_Main.title = "Чт."
//            5 -> (activity as MainActivity).ToolBar_Main.title = "Пт."
//            6 -> (activity as MainActivity).ToolBar_Main.title = "Сб."
//            7 -> (activity as MainActivity).ToolBar_Main.title = "Вс."
//        }
//    }





//    class DayItem(val timetable : Array<String>): Item<GroupieViewHolder>(){
//        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//            //viewHolder.itemView.textView3.text = position.toString()
//            val adapter = GroupAdapter<GroupieViewHolder>()
//
//
//
//            //val lol = AddLessonActivity.hometaskitem("Заголовок","Контент","cheburek")
//            timetable.forEach {
//                adapter.add(LessonItem(it.toString()){})
//                Log.d("DBlog", "Адаптер есть1")
//            }
//
//            Log.d("DBlog", "Адаптер есть2")
//
//            viewHolder.itemView.recycleview_TimetableFragment.adapter=adapter
//
//            adapter.setOnItemClickListener(OnItemClickListener { item, view ->
//                //Toast.makeText(this@MainFragment, "Пожалуйста, введите недостающие данные", Toast.LENGTH_SHORT).show()
//                Log.d("DBlog", "HHHHHHHH")
//            })
//
//
////            viewHolder.itemView.recyclerView_Timetable.setOnClickListener {
////                Log.d("RecyclerLog", "Clicked : $it " )
////            }
////                Log.d("dd", "Clicked : $item")
////                val bottom = view.findViewById<View>(R.id.layoutBottomSheet)
////                val bottomSheetBehavior = BottomSheetBehavior.from(bottom)
////                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
////                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
////                    // bottom_navigation.visibility = View.GONE
////                    //bottom_navigation.animate().scaleX(0.toFloat()).scaleY(0.toFloat()).setDuration(1000).start()
////                    //bottom_navigation.animate().y((bottom_navigation.height+coordinatorlayout.height).toFloat()).setDuration(1000).start()
////                }
////                else {
////                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
////                    //bottom_navigation.visibility = View.VISIBLE
////                }
////            }
//
//
//        }
//
//        override fun getLayout(): Int {
//            return R.layout.day_fragment
//        }
//
//        // override fun getItemCount(): Int = 1
//    }



//
//    class LessonItem(val lesson : String, private val itemClickListener: (Int) -> Unit): Item<GroupieViewHolder>(){
//        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//            //Log.d("FireBaseLogging", "ONE")
//            viewHolder.itemView.textView2.text = lesson
//            //viewHolder.itemView.textView.text = hometaskitem.hometask.toString()
////            viewHolder.itemView.setOnClickListener{
////                Log.d("DBlog", "Clicked : ${it.toString()}")
////            }
//
//        }
//        private inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
//
//            init {
//                itemView.setOnClickListener {
//                    Log.d("logi", "Click!") // WORKS!!!
//                    itemClickListener(adapterPosition)
//                }
//            }
//        }
//
//        override fun isClickable() = true
//        override fun getLayout(): Int {
//            //Log.d("FireBaseLogging", "TWO")
//            return R.layout.lesson_row
//        }
//    }



//}
