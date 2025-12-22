package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Business Trip model.
 */
data class BusinessTrip(
    val id: Int = 0,
    @SerializedName("transaction_code")
    val transactionCode: String = "",
    @SerializedName("transaction_date")
    val transactionDate: String? = null,
    val purpose: String? = null,
    val location: String? = null,
    @SerializedName("destination_type")
    val destinationType: String? = null,
    @SerializedName("destination_city")
    val destinationCity: String? = null,
    @SerializedName("start_date")
    val startDate: String? = null,
    @SerializedName("end_date")
    val endDate: String? = null,
    @SerializedName("start_time")
    val startTime: String? = null,
    @SerializedName("end_time")
    val endTime: String? = null,
    val days: Int = 0,
    @SerializedName("allowance_per_day")
    val allowancePerDay: Double = 0.0,
    @SerializedName("total_allowance")
    val totalAllowance: Double = 0.0,
    @SerializedName("cash_advance")
    val cashAdvance: Double = 0.0,
    val status: String = "pending",
    @SerializedName("assigned_by")
    val assignedBy: String? = null,
    @SerializedName("acknowledged_by")
    val acknowledgedBy: String? = null,
    @SerializedName("approved_by")
    val approvedBy: String? = null
)

/**
 * Business Trip List Response.
 */
data class BusinessTripListResponse(
    val success: Boolean = false,
    val data: List<BusinessTrip> = emptyList(),
    val meta: PaginationMeta? = null
)

/**
 * Business Trip Detail Response.
 */
data class BusinessTripDetailResponse(
    val data: BusinessTrip
)
