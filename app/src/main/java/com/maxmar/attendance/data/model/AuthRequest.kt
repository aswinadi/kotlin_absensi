package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Login request body.
 */
data class LoginRequest(
    val username: String,
    val password: String,
    @SerializedName("device_name")
    val deviceName: String = "android-app"
)
