package com.maxmar.attendance.ui.screens.businesstrip

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxmar.attendance.data.model.AssignableUser
import com.maxmar.attendance.data.model.MasterDataItem
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors
import java.text.NumberFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

/**
 * Business Trip create form screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessTripFormScreen(
    onNavigateBack: () -> Unit = {},
    onSuccess: () -> Unit = {},
    viewModel: BusinessTripViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val appColors = LocalAppColors.current
    val context = LocalContext.current
    
    // Form fields
    var selectedPurposeId by remember { mutableIntStateOf(0) }
    var selectedPurposeName by remember { mutableStateOf("") }
    var selectedDestinationId by remember { mutableIntStateOf(0) }
    var selectedDestinationName by remember { mutableStateOf("") }
    var selectedDestinationCode by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var destinationCity by remember { mutableStateOf("") }
    var departureDate by remember { mutableStateOf("") }
    var departureTime by remember { mutableStateOf("") }
    var arrivalDate by remember { mutableStateOf("") }
    var arrivalTime by remember { mutableStateOf("") }
    var selectedAssignedById by remember { mutableIntStateOf(0) }
    var selectedAssignedByName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var cashAdvance by remember { mutableStateOf("") }
    
    // Calculate trip days
    val tripDays = remember(departureDate, arrivalDate) {
        if (departureDate.isNotEmpty() && arrivalDate.isNotEmpty()) {
            try {
                val start = LocalDate.parse(departureDate)
                val end = LocalDate.parse(arrivalDate)
                ChronoUnit.DAYS.between(start, end).toInt() + 1
            } catch (e: Exception) { 0 }
        } else 0
    }
    
    // Load master data on first composition
    LaunchedEffect(Unit) {
        viewModel.loadFormData()
    }
    
    // Note: Allowance fetching removed - cash advance is now manual input
    
    // Handle success
    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            Toast.makeText(context, "Perjalanan dinas berhasil dibuat", Toast.LENGTH_SHORT).show()
            viewModel.resetFormState()
            onSuccess()
        }
    }
    
    // Handle error
    LaunchedEffect(formState.error) {
        formState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearFormError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Perjalanan Dinas", fontWeight = FontWeight.Bold) },
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
                // Purpose dropdown
                DropdownField(
                    label = "Maksud Perdin *",
                    selectedValue = selectedPurposeName,
                    options = formState.purposes,
                    onOptionSelected = { item ->
                        selectedPurposeId = item.id
                        selectedPurposeName = item.name
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Destination type dropdown
                DropdownField(
                    label = "Tipe Tujuan *",
                    selectedValue = selectedDestinationName,
                    options = formState.destinations,
                    onOptionSelected = { item ->
                        selectedDestinationId = item.id
                        selectedDestinationName = item.name
                        selectedDestinationCode = item.code ?: item.name.lowercase()
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Location field
                FormField(
                    label = "Lokasi *",
                    value = location,
                    onValueChange = { location = it },
                    placeholder = "Contoh: Gedung A, Jl. Sudirman"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Destination city field
                FormField(
                    label = "Kota Tujuan",
                    value = destinationCity,
                    onValueChange = { destinationCity = it },
                    placeholder = "Contoh: Jakarta"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Departure date & time
                Row(modifier = Modifier.fillMaxWidth()) {
                    DatePickerField(
                        label = "Tgl Berangkat *",
                        value = departureDate,
                        onDateSelected = { departureDate = it },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    TimePickerField(
                        label = "Jam",
                        value = departureTime,
                        onTimeSelected = { departureTime = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Arrival date & time
                Row(modifier = Modifier.fillMaxWidth()) {
                    DatePickerField(
                        label = "Tgl Kembali *",
                        value = arrivalDate,
                        onDateSelected = { arrivalDate = it },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    TimePickerField(
                        label = "Jam",
                        value = arrivalTime,
                        onTimeSelected = { arrivalTime = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Assigned by dropdown
                UserDropdownField(
                    label = "Ditugaskan Oleh",
                    selectedValue = selectedAssignedByName,
                    options = formState.assignableUsers,
                    onOptionSelected = { user ->
                        selectedAssignedById = user.id
                        selectedAssignedByName = user.name
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Cash advance input (optional)
                CurrencyField(
                    label = "Uang Muka (Opsional)",
                    value = cashAdvance,
                    onValueChange = { cashAdvance = it },
                    placeholder = "0"
                )
                
                // Trip days info
                if (tripDays > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Durasi: $tripDays hari",
                        color = appColors.textSecondary,
                        fontSize = 14.sp
                    )
                }
                
                // Notes field
                FormField(
                    label = "Catatan",
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = "Catatan tambahan (opsional)",
                    minLines = 3
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Submit button
                Button(
                    onClick = {
                        viewModel.createBusinessTrip(
                            purposeId = selectedPurposeId,
                            location = location,
                            destinationId = selectedDestinationId,
                            destinationCity = destinationCity,
                            departureDate = departureDate,
                            departureTime = departureTime.ifEmpty { null },
                            arrivalDate = arrivalDate,
                            arrivalTime = arrivalTime.ifEmpty { null },
                            assignedBy = selectedAssignedById,
                            cashAdvance = cashAdvance.replace(".", "").replace(",", "").toDoubleOrNull() ?: 0.0,
                            notes = notes.ifEmpty { null }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !formState.isSubmitting && 
                             selectedPurposeId > 0 &&
                             selectedDestinationId > 0 &&
                             location.isNotBlank() && 
                             destinationCity.isNotBlank() &&
                             departureDate.isNotBlank() && 
                             arrivalDate.isNotBlank() &&
                             selectedAssignedById > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaxmarColors.Primary,
                        contentColor = Color.White,
                        disabledContainerColor = MaxmarColors.Primary.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (formState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Kirim Pengajuan",
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
private fun CurrencyField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    val appColors = LocalAppColors.current
    
    Column {
        Text(text = label, color = appColors.textPrimary, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // Only allow digits
                val filtered = newValue.filter { it.isDigit() }
                // Format with thousand separators
                val formatted = if (filtered.isNotEmpty()) {
                    NumberFormat.getNumberInstance(Locale("id", "ID")).format(filtered.toLong())
                } else ""
                onValueChange(formatted)
            },
            placeholder = { Text(text = "Rp $placeholder", color = appColors.textSecondary) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Text("Rp", color = appColors.textSecondary, fontWeight = FontWeight.Medium)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaxmarColors.Primary,
                unfocusedBorderColor = appColors.textSecondary.copy(alpha = 0.3f),
                focusedContainerColor = appColors.cardBackground,
                unfocusedContainerColor = appColors.cardBackground,
                focusedTextColor = appColors.textPrimary,
                unfocusedTextColor = appColors.textPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}

@Composable
private fun DatePickerField(
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
            // Transparent clickable overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showPicker() }
            )
        }
    }
}

@Composable
private fun TimePickerField(
    label: String,
    value: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    
    val showPicker = {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                onTimeSelected(String.format("%02d:%02d", hour, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }
    
    Column(modifier = modifier) {
        Text(text = label, color = appColors.textPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            OutlinedTextField(
                value = value.ifEmpty { "--:--" },
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Default.Schedule, contentDescription = "Time", tint = appColors.textSecondary)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = appColors.textSecondary.copy(alpha = 0.3f),
                    disabledContainerColor = appColors.cardBackground,
                    disabledTextColor = if (value.isEmpty()) appColors.textSecondary else appColors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )
            // Transparent clickable overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showPicker() }
            )
        }
    }
}

@Composable
private fun DropdownField(
    label: String,
    selectedValue: String,
    options: List<MasterDataItem>,
    onOptionSelected: (MasterDataItem) -> Unit
) {
    val appColors = LocalAppColors.current
    var expanded by remember { mutableStateOf(false) }
    
    Column {
        Text(text = label, color = appColors.textPrimary, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            OutlinedTextField(
                value = selectedValue.ifEmpty { "Pilih..." },
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaxmarColors.Primary,
                    unfocusedBorderColor = appColors.textSecondary.copy(alpha = 0.3f),
                    focusedContainerColor = appColors.cardBackground,
                    unfocusedContainerColor = appColors.cardBackground,
                    focusedTextColor = if (selectedValue.isEmpty()) appColors.textSecondary else appColors.textPrimary,
                    unfocusedTextColor = if (selectedValue.isEmpty()) appColors.textSecondary else appColors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.name) },
                        onClick = {
                            onOptionSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun UserDropdownField(
    label: String,
    selectedValue: String,
    options: List<AssignableUser>,
    onOptionSelected: (AssignableUser) -> Unit
) {
    val appColors = LocalAppColors.current
    var expanded by remember { mutableStateOf(false) }
    
    Column {
        Text(text = label, color = appColors.textPrimary, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            OutlinedTextField(
                value = selectedValue.ifEmpty { "Pilih..." },
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaxmarColors.Primary,
                    unfocusedBorderColor = appColors.textSecondary.copy(alpha = 0.3f),
                    focusedContainerColor = appColors.cardBackground,
                    unfocusedContainerColor = appColors.cardBackground,
                    focusedTextColor = if (selectedValue.isEmpty()) appColors.textSecondary else appColors.textPrimary,
                    unfocusedTextColor = if (selectedValue.isEmpty()) appColors.textSecondary else appColors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { user ->
                    DropdownMenuItem(
                        text = { Text(user.name) },
                        onClick = {
                            onOptionSelected(user)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1
) {
    val appColors = LocalAppColors.current
    
    Column {
        Text(text = label, color = appColors.textPrimary, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = appColors.textSecondary) },
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
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
    }
}
