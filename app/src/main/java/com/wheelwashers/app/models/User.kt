package com.wheelwashers.app.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val createdAt: Long = 0L,
    val userType: String = "customer", // customer, admin, or washer
    val profileImageUrl: String = ""
) : Parcelable
