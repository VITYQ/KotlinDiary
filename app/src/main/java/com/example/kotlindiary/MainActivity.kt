package com.example.kotlindiary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.kotlindiary.loginregister.RegisterActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.ToolBar_Main
import kotlinx.android.synthetic.main.activity_main_coordinator.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_main.*
import java.util.*

class MainActivity : AppCompatActivity() {


    lateinit var mainFragment: MainFragment

    val a = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_coordinator)

        val uid = FirebaseAuth.getInstance().uid
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
                            editText_hometask_bottom.requestFocus()
                            //key.showSoftInput(editText_hometask_bottom, 0)
                            key.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                        }
                        else if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                            val key = baseContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            key.hideSoftInputFromWindow(editText_hometask_bottom.windowToken, 0)
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
                        R.id.two -> {
                            //deleteTabLayoutMargin()
                            supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.frame_layout, SettingsFragment())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit()
                            return@OnNavigationItemSelectedListener true
                        }
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
        if(bottomsheet.state != BottomSheetBehavior.STATE_COLLAPSED){
            val key = baseContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            key.hideSoftInputFromWindow(editText_hometask_bottom.windowToken, 0)
            bottomsheet.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
        else{
            super.onBackPressed()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_add ->{

                val key = baseContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                bottomsheet.state = BottomSheetBehavior.STATE_EXPANDED
                editText_hometask_bottom.postDelayed(Runnable(){
                    run(){
                        editText_hometask_bottom.requestFocus()//TODO: дальше разбираться с клавиатурой
                        //key.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    }
                }, 100)
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
}


//fun Activity.setTabLayoutMargin(){
//    tabLayoutMain.visibility = View.VISIBLE
//    var margin = frame_layout.layoutParams as ViewGroup.MarginLayoutParams
//    margin.setMargins(0, getPixelValue(this, 104), 0, getPixelValue(this, 56))
//    frame_layout.layoutParams = margin
//}
//
//
//fun Activity.deleteTabLayoutMargin(){
//    tabLayoutMain.visibility = View.GONE
//    var margin = frame_layout.layoutParams as ViewGroup.MarginLayoutParams
//    margin.setMargins(0, getPixelValue(this, 56), 0, getPixelValue(this, 56))
//    frame_layout.layoutParams = margin
//}


fun getPixelValue(context: Context, dimenId: Int): Int { //перевод dp в px для настройки отступов tablayout
    val resources: Resources = context.resources
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimenId.toFloat(), resources.getDisplayMetrics()).toInt()
}




//    private fun verifyIsUserLoggedIn(){
//        val uid = FirebaseAuth.getInstance().uid
//        if (uid == null){
//            val intent = Intent( this, RegisterActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }
//    }
//}
