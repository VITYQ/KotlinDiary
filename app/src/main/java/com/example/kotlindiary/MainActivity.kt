package com.example.kotlindiary

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.kotlindiary.loginregister.RegisterActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.ToolBar_Main
import kotlinx.android.synthetic.main.activity_main_coordinator.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_main.*

class MainActivity : AppCompatActivity() {


    lateinit var mainFragment: MainFragment

    val a = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_coordinator)
        //verifyIsUserLoggedIn()
        val uid = FirebaseAuth.getInstance().uid
        Log.d("BBBDLO", uid.toString())
        if (uid == null){

            val intent = Intent( this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        else {
            val bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, MainFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
            setSupportActionBar(findViewById(R.id.ToolBar_Main))

                bottomSheetBehavior.setBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        bottom_navigation.animate()
                            .y((coordinatorlayout.height - bottom_navigation.height + bottom_navigation.height * slideOffset).toFloat())
                            .setDuration(0).start()
                    }

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if(newState == BottomSheetBehavior.STATE_EXPANDED){

                            val key = baseContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            buttonCopyBottomSheet.setOnClickListener {
                                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("label", textView_hometask.text.toString())
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(this@MainActivity, "Скопировано", Toast.LENGTH_SHORT).show()
                            }
                            //editText_hometask_bottom.requestFocus()
                            //key.showSoftInput(editText_hometask_bottom, 0)
                            //key.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0) показываем клавиатуру
                        }
                        else if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                            Log.d("statecheck", "show on on change")
                            textView_hometask.text = ""
                            textView_lesson.text = ""
                            editText_hometask_bottom.text = Editable.Factory.getInstance().newEditable("")
                            val key = baseContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            key.hideSoftInputFromWindow(editText_hometask_bottom.windowToken, 0)
                            editText_hometask_bottom.clearFocus() //снимаем фокус, чтобы после перезапуска активити не открывалась автоматически клавиатура
                        }
                    }
                })
                bottom_navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener {
                    when (it.itemId) {
                        bottom_navigation.selectedItemId -> {
                            return@OnNavigationItemSelectedListener true
                        }
                        R.id.one -> {
                            //setTabLayoutMargin()
                            supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.frame_layout, MainFragment())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit()
                            return@OnNavigationItemSelectedListener true
                        }
//                        R.id.two -> {
//                            //deleteTabLayoutMargin()
//                            supportFragmentManager
//                                .beginTransaction()
//                                .replace(R.id.frame_layout, SettingsFragment())
//                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                                .commit()
//                            return@OnNavigationItemSelectedListener true
//                        }
                        R.id.three -> {
                            //deleteTabLayoutMargin()
                            ToolBar_Main.title = "Настройки"
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
        }
        coordinatorlayout.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            Log.d("hahaha", "view: $view, i: $i, i2: $i2, i3: $i3, i4: $i4, i5: $i5, i6: $i6, i7: $i7, i8: $i8")
            if(i8<i4){
                val bottomsheetmain = BottomSheetBehavior.from(layoutBottomSheet)
                Log.d("agada", "Keyboard gone")
                bottomsheetmain.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }
    override fun onBackPressed() {


        if(BottomSheetBehavior.from(layoutBottomSheet).state != BottomSheetBehavior.STATE_COLLAPSED){
            editText_hometask_bottom.clearFocus() //снимаем фокус, чтобы после перезапуска активити не открывалась автоматически клавиатура
            bottomsheet.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
        else{
            super.onBackPressed()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuInflater.inflate(R.menu.nav_menu, menu) TODO: добавить функционал меню
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menuChooseDay ->{
                val builder = MaterialDatePicker.Builder.datePicker()
                val picker = builder.build()
                picker.show(supportFragmentManager, picker.toString())
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun verifyIsUserLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent( this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

}


fun getPixelValue(context: Context, dimenId: Int): Int { //перевод dp в px для настройки отступов tablayout
    val resources: Resources = context.resources
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimenId.toFloat(), resources.getDisplayMetrics()).toInt()
}





