package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Update profile request body.
 */
data class UpdateProfileRequest(
    val phone: String,
    val nik: String,
    @SerializedName("permanent_address")
    val permanentAddress: String,
    @SerializedName("permanent_city")
    val permanentCity: String,
    @SerializedName("current_address")
    val currentAddress: String,
    @SerializedName("current_city")
    val currentCity: String
)
