package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Business Trip model.
 */
data class BusinessTrip(
    val id: Int,
    @SerializedName("transaction_code")
    val transactionCode: String,
    @SerializedName("transaction_date")
    val transactionDate: String,
    val purpose: String,
    val location: String,
    @SerializedName("destination_type")
    val destinationType: String,
    @SerializedName("destination_city")
    val destinationCity: String?,
    @SerializedName("start_date") // Mapped from departure_date in API resource usually
    val startDate: String,
    @SerializedName("end_date") // Mapped from arrival_date in API resource usually
    val endDate: String,
    @SerializedName("start_time")
    val startTime: String?,
    @SerializedName("end_time")
    val endTime: String?,
    val days: Int,
    @SerializedName("allowance_per_day")
    val allowancePerDay: Double,
    @SerializedName("total_allowance")
    val totalAllowance: Double,
    @SerializedName("cash_advance")
    val cashAdvance: Double,
    val status: String,
    @SerializedName("assigned_by")
    val assignedBy: ApprovalInfo?,
    @SerializedName("acknowledged_by")
    val acknowledgedBy: ApprovalInfo?,
    @SerializedName("approved_by")
    val approvedBy: ApprovalInfo?
)

/**
 * Business Trip List Response.
 */
data class BusinessTripListResponse(
    val data: List<BusinessTrip>,
    val meta: PaginationMeta?
)

/**
 * Business Trip Detail Response.
 */
data class BusinessTripDetailResponse(
    val data: BusinessTrip
)
