package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Employee info for approval.
 */
data class ApprovalEmployee(
    val id: Int,
    val name: String,
    val code: String?
)

/**
 * Absent type info for approval.
 */
data class ApprovalType(
    val id: Int,
    val name: String
)

/**
 * Approval item from API.
 */
data class Approval(
    val id: Int,
    val employee: ApprovalEmployee?,
    val type: ApprovalType?,
    val date: String,
    @SerializedName("date_display")
    val dateDisplay: String,
    val notes: String?,
    val status: String,
    val category: String? = "izin", // izin, perdin
    @SerializedName("acknowledged_by")
    val acknowledgedBy: String?,
    @SerializedName("acknowledged_date")
    val acknowledgedDate: String?,
    @SerializedName("approved_by")
    val approvedBy: String?,
    @SerializedName("approved_date")
    val approvedDate: String?,
    @SerializedName("created_at")
    val createdAt: String?
) {
    val statusDisplay: String
        get() = when (status) {
            "pending_acknowledgement" -> "Menunggu Diketahui"
            "pending_approval" -> "Menunggu Disetujui"
            "approved" -> "Disetujui"
            else -> status
        }
    
    val isPendingAcknowledgement: Boolean
        get() = status == "pending_acknowledgement"
    
    val isPendingApproval: Boolean
        get() = status == "pending_approval"
    
    val isApproved: Boolean
        get() = status == "approved"
}

/**
 * Approval list data wrapper.
 */
data class ApprovalListData(
    val pending: List<Approval>,
    val processed: List<Approval>
)

/**
 * Approval list response.
 */
data class ApprovalListResponse(
    val success: Boolean,
    val data: ApprovalListData?
)

/**
 * Approval action response.
 */
data class ApprovalActionResponse(
    val success: Boolean,
    val message: String?,
    val data: Approval?
)
