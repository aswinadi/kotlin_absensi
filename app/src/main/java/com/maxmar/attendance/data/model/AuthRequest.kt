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

/**
 * Change password request body.
 */
data class ChangePasswordRequest(
    @SerializedName("current_password")
    val currentPassword: String,
    @SerializedName("new_password")
    val newPassword: String,
    @SerializedName("new_password_confirmation")
    val newPasswordConfirmation: String
)
