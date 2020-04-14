package com.example.kotlindiary.loginregister

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.example.kotlindiary.MainActivity
import com.example.kotlindiary.R
import com.example.kotlindiary.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

   // private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //val inflater = LayoutInflater.from(this);
        //database = Firebase.database.reference

        button_Register.setOnClickListener(){
            performRegister()
        }
        button_Select_Photo.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        button_Login.setOnClickListener(){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

        var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){ //проверка на подходящее фото для кнопки выбора фото
            selectedPhotoUri = data.data
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            //var bitmapDrawable = BitmapDrawable(bitmap)
            selectphoto_ImageView_Register.setImageBitmap(bitmap)
            button_Select_Photo.alpha = 0f

//            button_Select_Photo.setBackgroundDrawable(bitmapDrawable)
              button_Select_Photo.setText("")

        }
    }

    private fun performRegister(){
        var username = editText_UsernameLogin.text.toString()
        var email = editText_Email.text.toString()
        var password = editText_PasswordLogin.text.toString()

        if(email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите недостающие данные", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity", "Email is " + email)
        Log.d("RegisterActivity", "Password is $password")
        Log.d("RegisterActivity", "Username is $username")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener
                if(it.isSuccessful){
                    // if(it.result.user.uid)
                    Log.d("RegisterActivity", "User succsessfully created with uid: ${it.result?.user?.uid}")
                    uploadeImageToFireBaseStorage()
                    //saveUserToFireBaseDatabase()
                }
            }
            .addOnFailureListener{
                Log.d("RegisterActivity", "Failed to create user ${it.message}")
                if(it.message.toString() == "The email address is badly formatted."){
                    Toast.makeText(this, "Неверно указан email адрес", Toast.LENGTH_SHORT).show()
                    Log.d("RegisterActivity", "Wrong mail")
                }
            }
    }

    var userImageUrl : String = ""

    private fun uploadeImageToFireBaseStorage(){
        if(selectedPhotoUri == null) {
            saveUserToFireBaseDatabase()
            return
        }
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully uploaded an image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    userImageUrl = it.toString()
                    Log.d("RegisterActivity", "imageurl: ${it.toString()}")
                    Log.d("RegisterActivity", "imageurl: $userImageUrl")
                    //Log.d("RegisterActivity", "imageurl: $userImageUrl")
                    saveUserToFireBaseDatabase()
                }
            }
            .addOnFailureListener{
                //что-нибудь для лога сюда можно вписать
            }
    }

    //Сохранение данных в firebase
    private fun saveUserToFireBaseDatabase(){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val url = userImageUrl
        Log.d("RegisterActivity", "I'm here")
        val user = User(uid, editText_UsernameLogin.text.toString(), url, editText_Email.text.toString(), "", "", "", "")
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Finally complete registration and save user to database")
                val intent = Intent(this, ProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d("RegisterActivity", "Something has gone wrong")
            }
    }
//    private fun saveUserToFireBaseDatabase(){
//        val uid = FirebaseAuth.getInstance().uid ?: ""
//
//        val user = User(uid, editText_UsernameLogin.text.toString(), "gh")
//        database.child("users").child(uid).setValue(user)
//            .addOnSuccessListener {
//                Log.d("RegisterActivity", "Successful add to database")
//            }
//            .addOnFailureListener{
//                Log.d("RegisterActivity", "${it.message}")
//            }
//    }


//    class User (val uid : String, val username : String, val profileImageUrl: String, val email: String, val name : String, val surname: String){
//        constructor() : this ("", "", "", "", "", "")
//    }

}
