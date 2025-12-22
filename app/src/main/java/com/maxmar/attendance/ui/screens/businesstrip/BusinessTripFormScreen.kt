package com.maxmar.attendance.ui.screens.businesstrip

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxmar.attendance.data.model.MasterDataItem
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors

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
    var location by remember { mutableStateOf("") }
    var destinationCity by remember { mutableStateOf("") }
    var departureDate by remember { mutableStateOf("") }
    var arrivalDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    // Load master data on first composition
    LaunchedEffect(Unit) {
        viewModel.loadFormData()
    }
    
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
                
                // Departure date field
                FormField(
                    label = "Tanggal Berangkat *",
                    value = departureDate,
                    onValueChange = { departureDate = it },
                    placeholder = "YYYY-MM-DD"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Arrival date field
                FormField(
                    label = "Tanggal Kembali *",
                    value = arrivalDate,
                    onValueChange = { arrivalDate = it },
                    placeholder = "YYYY-MM-DD"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
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
                            destinationCity = destinationCity.ifEmpty { null },
                            departureDate = departureDate,
                            arrivalDate = arrivalDate,
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
                             departureDate.isNotBlank() && 
                             arrivalDate.isNotBlank(),
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
private fun DropdownField(
    label: String,
    selectedValue: String,
    options: List<MasterDataItem>,
    onOptionSelected: (MasterDataItem) -> Unit
) {
    val appColors = LocalAppColors.current
    var expanded by remember { mutableStateOf(false) }
    
    Column {
        Text(
            text = label,
            color = appColors.textPrimary,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            OutlinedTextField(
                value = selectedValue.ifEmpty { "Pilih..." },
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
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
            // Invisible clickable overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
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
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1
) {
    val appColors = LocalAppColors.current
    
    Column {
        Text(
            text = label,
            color = appColors.textPrimary,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = appColors.textSecondary
                )
            },
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
