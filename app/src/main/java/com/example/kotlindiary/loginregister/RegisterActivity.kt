package com.example.kotlindiary.loginregister

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.kotlindiary.R
import com.example.kotlindiary.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_set_timetable.*
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
//        button_Select_Photo.setOnClickListener{
//            val intent = Intent(Intent.ACTION_PICK)
//            intent.type = "image/*"
//            startActivityForResult(intent, 0)
//        }
        button_Login.setOnClickListener(){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        textInputLayout_EmailRegister.editText?.doOnTextChanged { text, start, count, after ->
            textInputLayout_EmailRegister.error = null

        }
        textInputLayout_UsernameRegister.editText?.doOnTextChanged { text, start, count, after ->
            textInputLayout_UsernameRegister.error = null
        }
        textInputLayout_PasswordRegister.editText?.doOnTextChanged { text, start, count, after ->
            textInputLayout_PasswordRegister.error = null
        }
    }

        var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){ //проверка на подходящее фото для кнопки выбора фото
            selectedPhotoUri = data.data
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            //var bitmapDrawable = BitmapDrawable(bitmap)
            //selectphoto_ImageView_Register.setImageBitmap(bitmap)
            //button_Select_Photo.alpha = 0f

//            button_Select_Photo.setBackgroundDrawable(bitmapDrawable)
              //button_Select_Photo.setText("")

        }
    }

    private fun performRegister(){
        val username = textInputLayout_UsernameRegister.editText?.text.toString()
        val email = textInputLayout_EmailRegister.editText?.text.toString()
        val password = textInputLayout_PasswordRegister.editText?.text.toString()

        if(email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            if(email.isEmpty()){textInputLayout_EmailRegister.error = "Введите email"}
            if(username.isEmpty()){textInputLayout_UsernameRegister.error = "Введите имя пользователя"}
            if(password.isEmpty()){textInputLayout_PasswordRegister.error = "Введите пароль"}
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
                    //uploadeImageToFireBaseStorage()
                    saveUserToFireBaseDatabase()
                }
            }
            .addOnFailureListener{
                Log.d("RegisterActivity", "Failed to create user ${it.message}")
                if(it.message.toString() == "The email address is badly formatted."){
                    Toast.makeText(this, "Неверно указан email адрес", Toast.LENGTH_SHORT).show()
                    Log.d("RegisterActivity", "Wrong mail")
                    textInputLayout_EmailRegister.error = "Введите email"
                }
                else if(it.message.toString() == "Failed to create user The email address is already in use by another account."){
                    Toast.makeText(this, "Этот email уже занят", Toast.LENGTH_SHORT).show()
                    textInputLayout_EmailRegister.error ="Email занят"

                }

                else if(it.message.toString().contains("The given password", true)){
                    Toast.makeText(this, "В пароле должно быть минимум 6 символов", Toast.LENGTH_SHORT).show()
                    textInputLayout_PasswordRegister.error = "Пароль должен быть больше 6 символов"
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
                Log.d("loginregister", it.toString())
                //что-нибудь для лога сюда можно вписать
            }
    }

    //Сохранение данных в firebase
    private fun saveUserToFireBaseDatabase(){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val url = userImageUrl
        Log.d("RegisterActivity", "I'm here")
        val user = User(uid, textInputLayout_UsernameRegister.editText?.text.toString(), url, textInputLayout_EmailRegister.editText?.text.toString(), "", "", "", "")
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
