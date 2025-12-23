package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    // Category: "izin", "perdin"
    val category: String = "izin",
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
    
    val categoryDisplay: String
        get() = when (category) {
            "absent" -> "Izin"
            "business_trip" -> "Perdin"
            "realization" -> "Realisasi"
            else -> category
        }
    
    val isPendingAcknowledgement: Boolean
        get() = status == "pending_acknowledgement"
    
    val isPendingApproval: Boolean
        get() = status == "pending_approval"
    
    val isApproved: Boolean
        get() = status == "approved"
    
    /**
     * Check if user can edit this approval.
     * User can edit if:
     * 1. Start date is in the future (not today or past)
     * 2. No acknowledgement or approval has been given yet
     */
    val canEdit: Boolean
        get() {
            // Can only edit if still pending acknowledgement (no ack or approval)
            if (!isPendingAcknowledgement) return false
            
            // Check if start date is in the future
            return try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val startDate = dateFormat.parse(date)
                val today = dateFormat.parse(dateFormat.format(Date()))
                startDate != null && today != null && startDate.after(today)
            } catch (e: Exception) {
                false
            }
        }
}

/**
 * Approval list data wrapper.
 */
data class ApprovalListData(
    val pending: List<Approval>,
    val processed: List<Approval>,
    @SerializedName("is_manager")
    val isManager: Boolean = false
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
