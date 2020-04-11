package com.example.kotlindiary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (val uid : String, val username : String, val profileImageUrl: String, val email: String, val name : String, val surname: String, val school : String, val form : String): Parcelable{
    constructor() : this ("", "", "", "", "", "", "","")
}