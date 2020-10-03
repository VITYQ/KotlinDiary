package com.example.kotlindiary

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class FirebaseInstanceService : FirebaseInstanceIdService(){
    lateinit var name: String

    override fun onTokenRefresh() {
        // Mengambil token perangkat
        val token = FirebaseInstanceId.getInstance().token
        Log.d("ololol", "Token perangkat ini: ${token}")
    }
}