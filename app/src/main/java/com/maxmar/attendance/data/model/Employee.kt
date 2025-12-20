package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Office model with location data.
 */
data class Office(
    val id: Int,
    val name: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val radius: Int?
)

/**
 * Employee profile from API response.
 */
data class Employee(
    val id: Int,
    @SerializedName("employee_code")
    val employeeCode: String?,
    @SerializedName("full_name")
    val fullName: String,
    val email: String?,
    val phone: String?,
    val position: String?,
    val department: String?,
    @SerializedName("company_name")
    val companyName: String?,
    val office: Office?
)
