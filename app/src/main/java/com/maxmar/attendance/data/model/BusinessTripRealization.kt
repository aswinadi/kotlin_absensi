package com.maxmar.attendance.data.model

import com.google.gson.annotations.SerializedName

/**
 * Business Trip Realization data model.
 */
data class BusinessTripRealization(
    val id: Int,
    @SerializedName("transaction_code")
    val transactionCode: String,
    @SerializedName("transaction_date")
    val transactionDate: String?,
    @SerializedName("business_trip_id")
    val businessTripId: Int,
    @SerializedName("actual_departure_date")
    val actualDepartureDate: String?,
    @SerializedName("actual_arrival_date")
    val actualArrivalDate: String?,
    @SerializedName("actual_days")
    val actualDays: Int,
    // Transport expenses
    val transport: TransportExpense?,
    // Accommodation expenses
    val accommodation: AccommodationExpense?,
    // Other expenses
    val allowance: Double?,
    val meals: Double?,
    @SerializedName("meals_has_invoice")
    val mealsHasInvoice: Boolean?,
    @SerializedName("toll_fee")
    val tollFee: Double?,
    @SerializedName("toll_fee_has_invoice")
    val tollFeeHasInvoice: Boolean?,
    @SerializedName("parking_fee")
    val parkingFee: Double?,
    @SerializedName("parking_fee_has_invoice")
    val parkingFeeHasInvoice: Boolean?,
    @SerializedName("other_expense")
    val otherExpense: Double?,
    @SerializedName("other_expense_has_invoice")
    val otherExpenseHasInvoice: Boolean?,
    // Totals
    @SerializedName("total_expense")
    val totalExpense: Double,
    @SerializedName("cash_advance")
    val cashAdvance: Double,
    val difference: Double,
    val notes: String?,
    // Status
    val status: String,
    @SerializedName("acknowledged_by")
    val acknowledgedBy: String?,
    @SerializedName("acknowledged_date")
    val acknowledgedDate: String?,
    @SerializedName("approved_by")
    val approvedBy: String?,
    @SerializedName("approved_date")
    val approvedDate: String?,
    // Documents
    val documents: List<RealizationDocument>?
) {
    val statusDisplay: String
        get() = when (status) {
            "pending_acknowledgement" -> "Menunggu Diketahui"
            "pending_approval" -> "Menunggu Disetujui"
            "approved" -> "Disetujui"
            "rejected" -> "Ditolak"
            else -> status
        }
    
    val isPending: Boolean
        get() = status == "pending_acknowledgement" || status == "pending_approval"
    
    val canEdit: Boolean
        get() = status == "pending_acknowledgement"
}

/**
 * Transport expense breakdown.
 */
data class TransportExpense(
    val plane: Double?,
    @SerializedName("plane_has_invoice")
    val planeHasInvoice: Boolean?,
    val train: Double?,
    @SerializedName("train_has_invoice")
    val trainHasInvoice: Boolean?,
    val ship: Double?,
    @SerializedName("ship_has_invoice")
    val shipHasInvoice: Boolean?,
    @SerializedName("bus_taxi")
    val busTaxi: Double?,
    @SerializedName("bus_taxi_has_invoice")
    val busTaxiHasInvoice: Boolean?,
    val total: Double?
)

/**
 * Accommodation expense breakdown.
 */
data class AccommodationExpense(
    val hotel: Double?,
    @SerializedName("hotel_has_invoice")
    val hotelHasInvoice: Boolean?,
    @SerializedName("extra_bed")
    val extraBed: Double?,
    @SerializedName("extra_bed_has_invoice")
    val extraBedHasInvoice: Boolean?,
    val total: Double?
)

/**
 * Realization document model.
 */
data class RealizationDocument(
    val id: Int,
    val name: String,
    val url: String,
    val size: Long?
)

/**
 * Response wrapper for realization API.
 */
data class RealizationResponse(
    val success: Boolean,
    val message: String?,
    val data: BusinessTripRealization?
)

/**
 * Response wrapper for realization list.
 */
data class RealizationListResponse(
    val success: Boolean,
    val data: List<BusinessTripRealization>?
)
