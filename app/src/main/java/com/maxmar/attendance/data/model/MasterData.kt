package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Master data item (purpose or destination).
 */
data class MasterDataItem(
    val id: Int,
    val name: String,
    val code: String? = null,
    val description: String? = null
)

/**
 * Response for master data endpoints.
 */
data class MasterDataResponse(
    val success: Boolean,
    val data: List<MasterDataItem>
)

/**
 * Assignable user for dropdown.
 */
data class AssignableUser(
    val id: Int,
    val name: String,
    val email: String?
)

/**
 * Response for assignable users endpoint.
 */
data class AssignableUsersResponse(
    val success: Boolean,
    val data: List<AssignableUser>
)

/**
 * Allowance data.
 */
data class AllowanceData(
    @SerializedName("destination_type")
    val destinationType: String,
    @SerializedName("allowance_per_day")
    val allowancePerDay: Double
)

/**
 * Response for allowance endpoint.
 */
data class AllowanceResponse(
    val success: Boolean,
    val data: AllowanceData?
)

