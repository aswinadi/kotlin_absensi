package com.maxmar.attendance.ui.screens.realization

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmar.attendance.data.model.BusinessTrip
import com.maxmar.attendance.data.model.BusinessTripRealization
import com.maxmar.attendance.data.model.RealizationDocument
import com.maxmar.attendance.data.repository.AuthResult
import com.maxmar.attendance.data.repository.BusinessTripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

/**
 * State for trips needing realization list.
 */
data class RealizationListState(
    val isLoading: Boolean = false,
    val tripsNeedingRealization: List<BusinessTrip> = emptyList(),
    val error: String? = null
)

/**
 * State for realization form.
 */
data class RealizationFormState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val trip: BusinessTrip? = null,
    val existingRealization: BusinessTripRealization? = null,
    // Form fields
    val actualDepartureDate: String = "",
    val actualArrivalDate: String = "",
    // Transport
    val transportPlane: Double = 0.0,
    val transportPlaneHasInvoice: Boolean = false,
    val transportTrain: Double = 0.0,
    val transportTrainHasInvoice: Boolean = false,
    val transportShip: Double = 0.0,
    val transportShipHasInvoice: Boolean = false,
    val transportBusTaxi: Double = 0.0,
    val transportBusTaxiHasInvoice: Boolean = false,
    // Accommodation
    val accommodationHotel: Double = 0.0,
    val accommodationHotelHasInvoice: Boolean = false,
    val accommodationExtraBed: Double = 0.0,
    val accommodationExtraBedHasInvoice: Boolean = false,
    // Other
    val meals: Double = 0.0,
    val mealsHasInvoice: Boolean = false,
    val tollFee: Double = 0.0,
    val tollFeeHasInvoice: Boolean = false,
    val parkingFee: Double = 0.0,
    val parkingFeeHasInvoice: Boolean = false,
    val otherExpense: Double = 0.0,
    val otherExpenseHasInvoice: Boolean = false,
    val notes: String = "",
    // Documents
    val selectedDocuments: List<SelectedDocument> = emptyList(),
    val existingDocuments: List<RealizationDocument> = emptyList()
) {
    val transportTotal: Double
        get() = transportPlane + transportTrain + transportShip + transportBusTaxi
    
    val accommodationTotal: Double
        get() = accommodationHotel + accommodationExtraBed
    
    val totalExpense: Double
        get() = transportTotal + accommodationTotal + meals + tollFee + parkingFee + otherExpense
    
    val cashAdvance: Double
        get() = trip?.cashAdvance ?: 0.0
    
    val difference: Double
        get() = cashAdvance - totalExpense
}

/**
 * Selected document for upload.
 */
data class SelectedDocument(
    val uri: Uri,
    val name: String,
    val file: File? = null
)

/**
 * ViewModel for Business Trip Realization.
 */
@HiltViewModel
class BusinessTripRealizationViewModel @Inject constructor(
    private val repository: BusinessTripRepository
) : ViewModel() {
    
    private val _listState = MutableStateFlow(RealizationListState())
    val listState: StateFlow<RealizationListState> = _listState.asStateFlow()
    
    private val _formState = MutableStateFlow(RealizationFormState())
    val formState: StateFlow<RealizationFormState> = _formState.asStateFlow()
    
    /**
     * Load trips that need realization.
     */
    fun loadTripsNeedingRealization() {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)
            
            when (val result = repository.fetchTripsNeedingRealization()) {
                is AuthResult.Success -> {
                    _listState.value = _listState.value.copy(
                        isLoading = false,
                        tripsNeedingRealization = result.data
                    )
                }
                is AuthResult.Error -> {
                    _listState.value = _listState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    /**
     * Load trip detail and existing realization for form.
     */
    fun loadFormData(tripId: Int) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true, error = null)
            
            // Load trip detail
            when (val tripResult = repository.fetchBusinessTripDetail(tripId)) {
                is AuthResult.Success -> {
                    _formState.value = _formState.value.copy(
                        trip = tripResult.data,
                        actualDepartureDate = tripResult.data.startDate ?: "",
                        actualArrivalDate = tripResult.data.endDate ?: ""
                    )
                    
                    // Check for existing realization
                    when (val realizationResult = repository.fetchRealization(tripId)) {
                        is AuthResult.Success -> {
                            realizationResult.data?.let { realization ->
                                _formState.value = _formState.value.copy(
                                    existingRealization = realization,
                                    actualDepartureDate = realization.actualDepartureDate ?: "",
                                    actualArrivalDate = realization.actualArrivalDate ?: "",
                                    transportPlane = realization.transport?.plane ?: 0.0,
                                    transportPlaneHasInvoice = realization.transport?.planeHasInvoice ?: false,
                                    transportTrain = realization.transport?.train ?: 0.0,
                                    transportTrainHasInvoice = realization.transport?.trainHasInvoice ?: false,
                                    transportShip = realization.transport?.ship ?: 0.0,
                                    transportShipHasInvoice = realization.transport?.shipHasInvoice ?: false,
                                    transportBusTaxi = realization.transport?.busTaxi ?: 0.0,
                                    transportBusTaxiHasInvoice = realization.transport?.busTaxiHasInvoice ?: false,
                                    accommodationHotel = realization.accommodation?.hotel ?: 0.0,
                                    accommodationHotelHasInvoice = realization.accommodation?.hotelHasInvoice ?: false,
                                    accommodationExtraBed = realization.accommodation?.extraBed ?: 0.0,
                                    accommodationExtraBedHasInvoice = realization.accommodation?.extraBedHasInvoice ?: false,
                                    meals = realization.meals ?: 0.0,
                                    mealsHasInvoice = realization.mealsHasInvoice ?: false,
                                    tollFee = realization.tollFee ?: 0.0,
                                    tollFeeHasInvoice = realization.tollFeeHasInvoice ?: false,
                                    parkingFee = realization.parkingFee ?: 0.0,
                                    parkingFeeHasInvoice = realization.parkingFeeHasInvoice ?: false,
                                    otherExpense = realization.otherExpense ?: 0.0,
                                    otherExpenseHasInvoice = realization.otherExpenseHasInvoice ?: false,
                                    notes = realization.notes ?: "",
                                    existingDocuments = realization.documents ?: emptyList()
                                )
                            }
                            _formState.value = _formState.value.copy(isLoading = false)
                        }
                        is AuthResult.Error -> {
                            _formState.value = _formState.value.copy(isLoading = false)
                        }
                    }
                }
                is AuthResult.Error -> {
                    _formState.value = _formState.value.copy(
                        isLoading = false,
                        error = tripResult.message
                    )
                }
            }
        }
    }
    
    // Form field update functions
    fun updateActualDepartureDate(date: String) {
        _formState.value = _formState.value.copy(actualDepartureDate = date)
    }
    
    fun updateActualArrivalDate(date: String) {
        _formState.value = _formState.value.copy(actualArrivalDate = date)
    }
    
    fun updateTransportPlane(amount: Double, hasInvoice: Boolean) {
        _formState.value = _formState.value.copy(
            transportPlane = amount,
            transportPlaneHasInvoice = hasInvoice
        )
    }
    
    fun updateTransportTrain(amount: Double, hasInvoice: Boolean) {
        _formState.value = _formState.value.copy(
            transportTrain = amount,
            transportTrainHasInvoice = hasInvoice
        )
    }
    
    fun updateTransportShip(amount: Double, hasInvoice: Boolean) {
        _formState.value = _formState.value.copy(
            transportShip = amount,
            transportShipHasInvoice = hasInvoice
        )
    }
    
    fun updateTransportBusTaxi(amount: Double, hasInvoice: Boolean) {
        _formState.value = _formState.value.copy(
            transportBusTaxi = amount,
            transportBusTaxiHasInvoice = hasInvoice
        )
    }
    
    fun updateAccommodationHotel(amount: Double, hasInvoice: Boolean) {
        _formState.value = _formState.value.copy(
            accommodationHotel = amount,
            accommodationHotelHasInvoice = hasInvoice
        )
    }
    
    fun updateAccommodationExtraBed(amount: Double, hasInvoice: Boolean) {
        _formState.value = _formState.value.copy(
            accommodationExtraBed = amount,
            accommodationExtraBedHasInvoice = hasInvoice
        )
    }
    
    fun updateMeals(amount: Double, hasInvoice: Boolean) {
        _formState.value = _formState.value.copy(meals = amount, mealsHasInvoice = hasInvoice)
    }
    
    fun updateTollFee(amount: Double, hasInvoice: Boolean) {
        _formState.value = _formState.value.copy(tollFee = amount, tollFeeHasInvoice = hasInvoice)
    }
    
    fun updateParkingFee(amount: Double, hasInvoice: Boolean) {
        _formState.value = _formState.value.copy(parkingFee = amount, parkingFeeHasInvoice = hasInvoice)
    }
    
    fun updateOtherExpense(amount: Double, hasInvoice: Boolean) {
        _formState.value = _formState.value.copy(otherExpense = amount, otherExpenseHasInvoice = hasInvoice)
    }
    
    fun updateNotes(notes: String) {
        _formState.value = _formState.value.copy(notes = notes)
    }
    
    fun addDocument(uri: Uri, name: String, file: File?) {
        val current = _formState.value.selectedDocuments.toMutableList()
        current.add(SelectedDocument(uri, name, file))
        _formState.value = _formState.value.copy(selectedDocuments = current)
    }
    
    fun removeDocument(index: Int) {
        val current = _formState.value.selectedDocuments.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _formState.value = _formState.value.copy(selectedDocuments = current)
        }
    }
    
    fun removeExistingDocument(documentId: Int) {
        val tripId = _formState.value.trip?.id ?: return
        viewModelScope.launch {
            when (repository.deleteRealizationDocument(tripId, documentId)) {
                is AuthResult.Success -> {
                    val updated = _formState.value.existingDocuments.filter { it.id != documentId }
                    _formState.value = _formState.value.copy(existingDocuments = updated)
                }
                is AuthResult.Error -> {
                    // Handle error
                }
            }
        }
    }
    
    /**
     * Submit realization.
     */
    fun submitRealization() {
        val state = _formState.value
        val tripId = state.trip?.id ?: return
        
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isSubmitting = true, error = null)
            
            // Build multipart data
            val data = buildFormData(state)
            val documents = buildDocumentParts(state.selectedDocuments)
            
            val result = if (state.existingRealization != null) {
                repository.updateRealization(tripId, data, documents)
            } else {
                repository.createRealization(tripId, data, documents)
            }
            
            when (result) {
                is AuthResult.Success -> {
                    _formState.value = _formState.value.copy(
                        isSubmitting = false,
                        isSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    _formState.value = _formState.value.copy(
                        isSubmitting = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    private fun buildFormData(state: RealizationFormState): Map<String, RequestBody> {
        val data = mutableMapOf<String, RequestBody>()
        
        fun String.toTextBody(): RequestBody = this.toRequestBody("text/plain".toMediaTypeOrNull())
        fun Double.toTextBody(): RequestBody = this.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        fun Boolean.toTextBody(): RequestBody = (if (this) "1" else "0").toRequestBody("text/plain".toMediaTypeOrNull())
        
        data["actual_departure_date"] = state.actualDepartureDate.toTextBody()
        data["actual_arrival_date"] = state.actualArrivalDate.toTextBody()
        
        // Transport
        data["transport_plane"] = state.transportPlane.toTextBody()
        data["transport_plane_has_invoice"] = state.transportPlaneHasInvoice.toTextBody()
        data["transport_train"] = state.transportTrain.toTextBody()
        data["transport_train_has_invoice"] = state.transportTrainHasInvoice.toTextBody()
        data["transport_ship"] = state.transportShip.toTextBody()
        data["transport_ship_has_invoice"] = state.transportShipHasInvoice.toTextBody()
        data["transport_bus_taxi"] = state.transportBusTaxi.toTextBody()
        data["transport_bus_taxi_has_invoice"] = state.transportBusTaxiHasInvoice.toTextBody()
        
        // Accommodation
        data["accommodation_hotel"] = state.accommodationHotel.toTextBody()
        data["accommodation_hotel_has_invoice"] = state.accommodationHotelHasInvoice.toTextBody()
        data["accommodation_extra_bed"] = state.accommodationExtraBed.toTextBody()
        data["accommodation_extra_bed_has_invoice"] = state.accommodationExtraBedHasInvoice.toTextBody()
        
        // Other
        data["meals"] = state.meals.toTextBody()
        data["meals_has_invoice"] = state.mealsHasInvoice.toTextBody()
        data["toll_fee"] = state.tollFee.toTextBody()
        data["toll_fee_has_invoice"] = state.tollFeeHasInvoice.toTextBody()
        data["parking_fee"] = state.parkingFee.toTextBody()
        data["parking_fee_has_invoice"] = state.parkingFeeHasInvoice.toTextBody()
        data["other_expense"] = state.otherExpense.toTextBody()
        data["other_expense_has_invoice"] = state.otherExpenseHasInvoice.toTextBody()
        
        if (state.notes.isNotBlank()) {
            data["notes"] = state.notes.toTextBody()
        }
        
        return data
    }
    
    private fun buildDocumentParts(documents: List<SelectedDocument>): List<MultipartBody.Part>? {
        if (documents.isEmpty()) return null
        
        return documents.mapNotNull { doc ->
            doc.file?.let { file ->
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("documents[]", file.name, requestBody)
            }
        }.ifEmpty { null }
    }
    
    fun resetFormState() {
        _formState.value = RealizationFormState()
    }
    
    fun clearError() {
        _formState.value = _formState.value.copy(error = null)
    }
}
