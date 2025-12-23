package com.maxmar.attendance.ui.screens.realization

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors
import java.io.File
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

/**
 * Form screen for creating/editing realization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealizationFormScreen(
    tripId: Int,
    onNavigateBack: () -> Unit = {},
    onSuccess: () -> Unit = {},
    viewModel: BusinessTripRealizationViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val appColors = LocalAppColors.current
    val context = LocalContext.current
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    // Document picker launcher
    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            val fileName = "Document_${System.currentTimeMillis()}.jpg"
            // Copy to cache for upload
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, fileName)
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                viewModel.addDocument(uri, fileName, file)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    LaunchedEffect(tripId) {
        viewModel.loadFormData(tripId)
    }
    
    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            Toast.makeText(context, "Realisasi berhasil disimpan", Toast.LENGTH_SHORT).show()
            viewModel.resetFormState()
            onSuccess()
        }
    }
    
    LaunchedEffect(formState.error) {
        formState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (formState.existingRealization != null) "Edit Realisasi" else "Buat Realisasi",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appColors.surface,
                    titleContentColor = appColors.textPrimary,
                    navigationIconContentColor = appColors.textPrimary
                )
            )
        }
    ) { paddingValues ->
        if (formState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaxmarColors.Primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                appColors.backgroundGradientStart,
                                appColors.backgroundGradientEnd
                            )
                        )
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Trip info card (read-only)
                formState.trip?.let { trip ->
                    TripInfoCard(trip = trip)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Actual dates section
                SectionTitle("Tanggal Aktual")
                Row(modifier = Modifier.fillMaxWidth()) {
                    DateField(
                        label = "Berangkat",
                        value = formState.actualDepartureDate,
                        onDateSelected = { viewModel.updateActualDepartureDate(it) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    DateField(
                        label = "Kembali",
                        value = formState.actualArrivalDate,
                        onDateSelected = { viewModel.updateActualArrivalDate(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Transport section
                SectionTitle("Transportasi")
                ExpenseField(
                    label = "Pesawat",
                    amount = formState.transportPlane,
                    hasInvoice = formState.transportPlaneHasInvoice,
                    onAmountChange = { viewModel.updateTransportPlane(it, formState.transportPlaneHasInvoice) },
                    onInvoiceChange = { viewModel.updateTransportPlane(formState.transportPlane, it) }
                )
                ExpenseField(
                    label = "Kereta",
                    amount = formState.transportTrain,
                    hasInvoice = formState.transportTrainHasInvoice,
                    onAmountChange = { viewModel.updateTransportTrain(it, formState.transportTrainHasInvoice) },
                    onInvoiceChange = { viewModel.updateTransportTrain(formState.transportTrain, it) }
                )
                ExpenseField(
                    label = "Kapal",
                    amount = formState.transportShip,
                    hasInvoice = formState.transportShipHasInvoice,
                    onAmountChange = { viewModel.updateTransportShip(it, formState.transportShipHasInvoice) },
                    onInvoiceChange = { viewModel.updateTransportShip(formState.transportShip, it) }
                )
                ExpenseField(
                    label = "Bus/Taxi",
                    amount = formState.transportBusTaxi,
                    hasInvoice = formState.transportBusTaxiHasInvoice,
                    onAmountChange = { viewModel.updateTransportBusTaxi(it, formState.transportBusTaxiHasInvoice) },
                    onInvoiceChange = { viewModel.updateTransportBusTaxi(formState.transportBusTaxi, it) }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Accommodation section
                SectionTitle("Akomodasi")
                ExpenseField(
                    label = "Hotel",
                    amount = formState.accommodationHotel,
                    hasInvoice = formState.accommodationHotelHasInvoice,
                    onAmountChange = { viewModel.updateAccommodationHotel(it, formState.accommodationHotelHasInvoice) },
                    onInvoiceChange = { viewModel.updateAccommodationHotel(formState.accommodationHotel, it) }
                )
                ExpenseField(
                    label = "Extra Bed",
                    amount = formState.accommodationExtraBed,
                    hasInvoice = formState.accommodationExtraBedHasInvoice,
                    onAmountChange = { viewModel.updateAccommodationExtraBed(it, formState.accommodationExtraBedHasInvoice) },
                    onInvoiceChange = { viewModel.updateAccommodationExtraBed(formState.accommodationExtraBed, it) }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Other expenses section
                SectionTitle("Pengeluaran Lain")
                ExpenseField(
                    label = "Makan",
                    amount = formState.meals,
                    hasInvoice = formState.mealsHasInvoice,
                    onAmountChange = { viewModel.updateMeals(it, formState.mealsHasInvoice) },
                    onInvoiceChange = { viewModel.updateMeals(formState.meals, it) }
                )
                ExpenseField(
                    label = "Tol",
                    amount = formState.tollFee,
                    hasInvoice = formState.tollFeeHasInvoice,
                    onAmountChange = { viewModel.updateTollFee(it, formState.tollFeeHasInvoice) },
                    onInvoiceChange = { viewModel.updateTollFee(formState.tollFee, it) }
                )
                ExpenseField(
                    label = "Parkir",
                    amount = formState.parkingFee,
                    hasInvoice = formState.parkingFeeHasInvoice,
                    onAmountChange = { viewModel.updateParkingFee(it, formState.parkingFeeHasInvoice) },
                    onInvoiceChange = { viewModel.updateParkingFee(formState.parkingFee, it) }
                )
                ExpenseField(
                    label = "Lainnya",
                    amount = formState.otherExpense,
                    hasInvoice = formState.otherExpenseHasInvoice,
                    onAmountChange = { viewModel.updateOtherExpense(it, formState.otherExpenseHasInvoice) },
                    onInvoiceChange = { viewModel.updateOtherExpense(formState.otherExpense, it) }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Documents section
                SectionTitle("Dokumen Pendukung")
                DocumentsSection(
                    selectedDocuments = formState.selectedDocuments,
                    existingDocuments = formState.existingDocuments,
                    onAddDocument = { documentLauncher.launch("image/*") },
                    onRemoveSelected = { viewModel.removeDocument(it) },
                    onRemoveExisting = { viewModel.removeExistingDocument(it) }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Notes
                SectionTitle("Catatan")
                OutlinedTextField(
                    value = formState.notes,
                    onValueChange = { viewModel.updateNotes(it) },
                    placeholder = { Text("Catatan tambahan (opsional)", color = appColors.textSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaxmarColors.Primary,
                        unfocusedBorderColor = appColors.textSecondary.copy(alpha = 0.3f),
                        focusedContainerColor = appColors.cardBackground,
                        unfocusedContainerColor = appColors.cardBackground,
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Summary card
                SummaryCard(
                    transportTotal = formState.transportTotal,
                    accommodationTotal = formState.accommodationTotal,
                    meals = formState.meals,
                    tollFee = formState.tollFee,
                    parkingFee = formState.parkingFee,
                    otherExpense = formState.otherExpense,
                    totalExpense = formState.totalExpense,
                    cashAdvance = formState.cashAdvance,
                    difference = formState.difference
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Submit button
                Button(
                    onClick = { viewModel.submitRealization() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !formState.isSubmitting && 
                             formState.actualDepartureDate.isNotBlank() &&
                             formState.actualArrivalDate.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaxmarColors.Primary,
                        contentColor = Color.White,
                        disabledContainerColor = MaxmarColors.Primary.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (formState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (formState.existingRealization != null) "Update Realisasi" else "Simpan Realisasi",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun TripInfoCard(trip: com.maxmar.attendance.data.model.BusinessTrip) {
    val appColors = LocalAppColors.current
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = appColors.cardBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = trip.transactionCode,
                fontWeight = FontWeight.Bold,
                color = appColors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            trip.purpose?.let {
                Text(text = it, color = appColors.textSecondary, fontSize = 14.sp)
            }
            Text(
                text = "${trip.location} - ${trip.destinationCity ?: ""}",
                color = appColors.textSecondary,
                fontSize = 14.sp
            )
            Text(
                text = "${trip.startDate} - ${trip.endDate} (${trip.days} hari)",
                color = appColors.textSecondary,
                fontSize = 14.sp
            )
            if ((trip.cashAdvance ?: 0.0) > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Uang Muka: ${formatter.format(trip.cashAdvance)}",
                    color = MaxmarColors.Primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    val appColors = LocalAppColors.current
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = appColors.textPrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun DateField(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    
    val showPicker = {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                onDateSelected(String.format("%04d-%02d-%02d", year, month + 1, day))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    Column(modifier = modifier) {
        Text(text = label, color = appColors.textPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            OutlinedTextField(
                value = value.ifEmpty { "Pilih tanggal" },
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar", tint = appColors.textSecondary)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = appColors.textSecondary.copy(alpha = 0.3f),
                    disabledContainerColor = appColors.cardBackground,
                    disabledTextColor = if (value.isEmpty()) appColors.textSecondary else appColors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Box(modifier = Modifier.matchParentSize().clickable { showPicker() })
        }
    }
}

@Composable
private fun ExpenseField(
    label: String,
    amount: Double,
    hasInvoice: Boolean,
    onAmountChange: (Double) -> Unit,
    onInvoiceChange: (Boolean) -> Unit
) {
    val appColors = LocalAppColors.current
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = if (amount > 0) formatter.format(amount.toLong()) else "",
            onValueChange = { newValue ->
                val filtered = newValue.filter { it.isDigit() }
                onAmountChange(filtered.toDoubleOrNull() ?: 0.0)
            },
            label = { Text(label) },
            placeholder = { Text("0", color = appColors.textSecondary) },
            modifier = Modifier.weight(1f),
            leadingIcon = { Text("Rp", color = appColors.textSecondary, fontWeight = FontWeight.Medium) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaxmarColors.Primary,
                unfocusedBorderColor = appColors.textSecondary.copy(alpha = 0.3f),
                focusedContainerColor = appColors.cardBackground,
                unfocusedContainerColor = appColors.cardBackground
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = hasInvoice,
                onCheckedChange = onInvoiceChange,
                colors = CheckboxDefaults.colors(checkedColor = MaxmarColors.Primary)
            )
            Text("Invoice", fontSize = 12.sp, color = appColors.textSecondary)
        }
    }
}

@Composable
private fun DocumentsSection(
    selectedDocuments: List<SelectedDocument>,
    existingDocuments: List<com.maxmar.attendance.data.model.RealizationDocument>,
    onAddDocument: () -> Unit,
    onRemoveSelected: (Int) -> Unit,
    onRemoveExisting: (Int) -> Unit
) {
    val appColors = LocalAppColors.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Add button
        OutlinedCard(
            modifier = Modifier
                .size(80.dp)
                .clickable(onClick = onAddDocument),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = MaxmarColors.Primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        
        // Existing documents
        existingDocuments.forEach { doc ->
            Box {
                OutlinedCard(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = appColors.textSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                IconButton(
                    onClick = { onRemoveExisting(doc.id) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .background(Color.Red, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        
        // Selected documents
        selectedDocuments.forEachIndexed { index, doc ->
            Box {
                OutlinedCard(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MaxmarColors.Primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "New",
                                fontSize = 10.sp,
                                color = MaxmarColors.Primary
                            )
                        }
                    }
                }
                IconButton(
                    onClick = { onRemoveSelected(index) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .background(Color.Red, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    transportTotal: Double,
    accommodationTotal: Double,
    meals: Double,
    tollFee: Double,
    parkingFee: Double,
    otherExpense: Double,
    totalExpense: Double,
    cashAdvance: Double,
    difference: Double
) {
    val appColors = LocalAppColors.current
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = appColors.cardBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "RINGKASAN BIAYA",
                fontWeight = FontWeight.Bold,
                color = appColors.textPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            SummaryRow("Transport", transportTotal, formatter)
            SummaryRow("Akomodasi", accommodationTotal, formatter)
            SummaryRow("Makan", meals, formatter)
            SummaryRow("Tol", tollFee, formatter)
            SummaryRow("Parkir", parkingFee, formatter)
            SummaryRow("Lainnya", otherExpense, formatter)
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            SummaryRow("TOTAL PENGELUARAN", totalExpense, formatter, true)
            SummaryRow("Uang Muka Diterima", cashAdvance, formatter)
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (difference >= 0) "SELISIH (Kembalikan)" else "SELISIH (Reimburse)",
                    fontWeight = FontWeight.Bold,
                    color = appColors.textPrimary
                )
                Text(
                    text = formatter.format(kotlin.math.abs(difference)),
                    fontWeight = FontWeight.Bold,
                    color = if (difference >= 0) MaxmarColors.Success else MaxmarColors.Error
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    amount: Double,
    formatter: NumberFormat,
    isBold: Boolean = false
) {
    val appColors = LocalAppColors.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = appColors.textSecondary,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = formatter.format(amount),
            color = appColors.textPrimary,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}
