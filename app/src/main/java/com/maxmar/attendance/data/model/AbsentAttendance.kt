package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Absent type from API.
 */
data class AbsentType(
    val id: Int,
    val code: String,
    val name: String,
    val color: String?,
    @SerializedName("requires_attachment")
    val requiresAttachment: Boolean
)

/**
 * Absent types API response.
 */
data class AbsentTypesResponse(
    val success: Boolean,
    val data: List<AbsentType>
)

/**
 * Absent attendance status.
 */
enum class AbsentStatus(val value: String) {
    @SerializedName("pending_acknowledgement")
    PENDING_ACKNOWLEDGEMENT("pending_acknowledgement"),
    @SerializedName("pending_approval")
    PENDING_APPROVAL("pending_approval"),
    @SerializedName("approved")
    APPROVED("approved")
}

/**
 * Absent attendance type info.
 */
data class AbsentTypeInfo(
    val code: String?,
    val name: String?,
    val color: String?
)

/**
 * Absent attendance record.
 */
data class AbsentAttendance(
    val id: Int,
    val date: String,
    val type: AbsentTypeInfo?,
    val notes: String?,
    @SerializedName("has_attachment")
    val hasAttachment: Boolean,
    val status: String,
    val employee: Employee? = null,
    val acknowledged: ApprovalInfo?,
    val approved: ApprovalInfo?
) {
    val isPendingAcknowledgement: Boolean get() = status == "pending_acknowledgement"
    val isPendingApproval: Boolean get() = status == "pending_approval"
    val isApproved: Boolean get() = status == "approved"
}

/**
 * Approval info (acknowledged/approved by).
 */
data class ApprovalInfo(
    val by: String?,
    val date: String?,
    val notes: String? = null
)

/**
 * Absent attendance list response.
 */
data class AbsentAttendanceListResponse(
    val success: Boolean,
    val data: List<AbsentAttendance>,
    val meta: PaginationMeta?
)

/**
 * Absent attendance create/update response.
 */
data class AbsentAttendanceResponse(
    val success: Boolean,
    val message: String?,
    val data: AbsentAttendanceResult?
)

/**
 * Absent attendance detail response.
 */
data class AbsentAttendanceDetailResponse(
    val success: Boolean,
    val message: String?,
    val data: AbsentAttendance?
)

/**
 * Result data from create/update.
 */
data class AbsentAttendanceResult(
    val id: Int,
    val date: String,
    val type: String?,
    @SerializedName("has_attachment")
    val hasAttachment: Boolean,
    @SerializedName("attachments_count")
    val attachmentsCount: Int?,
    val status: String?
)
