package com.maxmar.attendance.data.model

/**
 * Master data item (purpose or destination).
 */
data class MasterDataItem(
    val id: Int,
    val name: String,
    val description: String? = null
)

/**
 * Response for master data endpoints.
 */
data class MasterDataResponse(
    val success: Boolean,
    val data: List<MasterDataItem>
)
