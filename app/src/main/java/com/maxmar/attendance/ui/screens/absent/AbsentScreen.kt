package com.maxmar.attendance.ui.screens.absent

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxmar.attendance.data.model.AbsentType
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Absent form screen for submitting leave/sick requests.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsentScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AbsentViewModel = hiltViewModel()
) {
    val state by viewModel.formState.collectAsState()
    val context = LocalContext.current
    val appColors = LocalAppColors.current
    
    // Handle success
    LaunchedEffect(state.submitSuccess) {
        if (state.submitSuccess) {
            Toast.makeText(context, "Pengajuan berhasil dikirim", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    }
    
    // Handle error
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pengajuan Ketidakhadiran",
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
                    containerColor = appColors.backgroundGradientStart,
                    titleContentColor = appColors.textPrimary,
                    navigationIconContentColor = appColors.textPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            appColors.backgroundGradientStart,
                            appColors.backgroundGradientEnd
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            if (state.isLoadingTypes) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaxmarColors.Primary
                )
            } else {
                AbsentFormContent(
                    state = state,
                    onTypeSelected = viewModel::selectAbsentType,
                    onDateChanged = viewModel::setDate,
                    onNotesChanged = viewModel::setNotes,
                    onAttachmentSelected = viewModel::setAttachment,
                    onSubmit = viewModel::submitForm
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AbsentFormContent(
    state: AbsentFormState,
    onTypeSelected: (AbsentType) -> Unit,
    onDateChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onAttachmentSelected: (File?) -> Unit,
    onSubmit: () -> Unit
) {
    val appColors = LocalAppColors.current
    val context = LocalContext.current
    var typeExpanded by remember { mutableStateOf(false) }
    
    // Date picker
    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayDateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            onDateChanged(dateFormatter.format(calendar.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    
    // File picker
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Copy to cache and get file
            val inputStream = context.contentResolver.openInputStream(it)
            val file = File(context.cacheDir, "attachment_${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            onAttachmentSelected(file)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = appColors.surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Absent Type Dropdown
                Text(
                    text = "Jenis Ketidakhadiran",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = appColors.textPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.selectedType?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Pilih jenis ketidakhadiran") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaxmarColors.Primary,
                            unfocusedBorderColor = appColors.textTertiary.copy(alpha = 0.3f),
                            focusedTextColor = appColors.textPrimary,
                            unfocusedTextColor = appColors.textPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        state.absentTypes.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(type.name, color = appColors.textPrimary)
                                        if (type.requiresAttachment) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "(Perlu surat dokter)",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = appColors.textSecondary
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    onTypeSelected(type)
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Date Picker
                Text(
                    text = "Tanggal",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = appColors.textPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = if (state.selectedDate.isNotBlank()) {
                        try {
                            val date = dateFormatter.parse(state.selectedDate)
                            displayDateFormatter.format(date!!)
                        } catch (e: Exception) {
                            state.selectedDate
                        }
                    } else "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Pilih tanggal") },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Pick date",
                                tint = MaxmarColors.Primary
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaxmarColors.Primary,
                        unfocusedBorderColor = appColors.textTertiary.copy(alpha = 0.3f),
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Notes
                Text(
                    text = "Keterangan (Opsional)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = appColors.textPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = onNotesChanged,
                    placeholder = { Text("Tambahkan keterangan...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaxmarColors.Primary,
                        unfocusedBorderColor = appColors.textTertiary.copy(alpha = 0.3f),
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Attachment (show if requires attachment or always show as optional)
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = if (state.selectedType?.requiresAttachment == true) 
                        "Surat Keterangan Dokter (Wajib)" 
                    else 
                        "Lampiran (Opsional)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = appColors.textPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (state.attachmentFile != null) {
                    // Show selected file
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaxmarColors.Primary.copy(alpha = 0.1f))
                            .border(1.dp, MaxmarColors.Primary, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaxmarColors.Primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = state.attachmentFile.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = appColors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { onAttachmentSelected(null) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = MaxmarColors.Error
                            )
                        }
                    }
                } else {
                    // Show upload button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                2.dp,
                                appColors.textTertiary.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { fileLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = "Attach file",
                                tint = appColors.textSecondary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap untuk memilih file",
                                style = MaterialTheme.typography.bodyMedium,
                                color = appColors.textSecondary
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Submit Button
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !state.isSubmitting,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaxmarColors.Primary,
                disabledContainerColor = MaxmarColors.Primary.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Kirim Pengajuan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
