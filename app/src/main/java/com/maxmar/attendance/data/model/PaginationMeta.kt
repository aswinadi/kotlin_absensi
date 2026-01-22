package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Pagination metadata for paginated API responses.
 */
data class PaginationMeta(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("last_page")
    val lastPage: Int,
    @SerializedName("per_page")
    val perPage: Int,
    val total: Int
)
