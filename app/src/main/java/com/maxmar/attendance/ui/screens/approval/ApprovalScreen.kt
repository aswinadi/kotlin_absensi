package com.maxmar.attendance.ui.screens.approval

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxmar.attendance.data.model.Approval
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors

/**
 * Approval list screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApprovalScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ApprovalViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val appColors = LocalAppColors.current
    val context = LocalContext.current
    
    var showRejectDialog by remember { mutableStateOf<Approval?>(null) }
    var rejectReason by remember { mutableStateOf("") }
    
    // Handle action messages
    LaunchedEffect(state.actionSuccess) {
        state.actionSuccess?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearActionMessages()
        }
    }
    
    LaunchedEffect(state.actionError) {
        state.actionError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearActionMessages()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Persetujuan", fontWeight = FontWeight.Bold) },
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
        ) {
            // Filter chips
            FilterChipsRow(
                selectedFilter = state.selectedFilter,
                pendingCount = state.pendingItems.size,
                onFilterSelected = { viewModel.setFilter(it) }
            )
            
            // List
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaxmarColors.Primary)
                }
            } else {
                val items = if (state.selectedFilter == "pending") {
                    state.pendingItems
                } else {
                    state.processedItems
                }
                
                if (items.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.HourglassEmpty,
                                contentDescription = null,
                                tint = appColors.textSecondary,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (state.selectedFilter == "pending") 
                                    "Tidak ada persetujuan menunggu" 
                                else 
                                    "Tidak ada riwayat persetujuan",
                                color = appColors.textSecondary
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items, key = { it.id }) { approval ->
                            ApprovalCard(
                                approval = approval,
                                onAcknowledge = { viewModel.acknowledge(approval) },
                                onApprove = { viewModel.approve(approval) },
                                onReject = { showRejectDialog = approval }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Reject dialog
    if (showRejectDialog != null) {
        AlertDialog(
            onDismissRequest = { 
                showRejectDialog = null
                rejectReason = ""
            },
            title = { Text("Tolak Pengajuan") },
            text = {
                Column {
                    Text("Apakah Anda yakin ingin menolak pengajuan ini?")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        label = { Text("Alasan (opsional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRejectDialog?.let { viewModel.reject(it, rejectReason.ifEmpty { null }) }
                        showRejectDialog = null
                        rejectReason = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaxmarColors.Error)
                ) {
                    Text("Tolak")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showRejectDialog = null
                    rejectReason = ""
                }) {
                    Text("Batal")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChipsRow(
    selectedFilter: String,
    pendingCount: Int,
    onFilterSelected: (String) -> Unit
) {
    val appColors = LocalAppColors.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == "pending",
            onClick = { onFilterSelected("pending") },
            label = { 
                Text(
                    if (pendingCount > 0) "Menunggu ($pendingCount)" else "Menunggu"
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaxmarColors.Warning,
                selectedLabelColor = Color.White,
                containerColor = appColors.cardBackground,
                labelColor = appColors.textSecondary
            )
        )
        
        FilterChip(
            selected = selectedFilter == "processed",
            onClick = { onFilterSelected("processed") },
            label = { Text("Selesai") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaxmarColors.Success,
                selectedLabelColor = Color.White,
                containerColor = appColors.cardBackground,
                labelColor = appColors.textSecondary
            )
        )
    }
}

@Composable
private fun ApprovalCard(
    approval: Approval,
    onAcknowledge: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val appColors = LocalAppColors.current
    
    val statusColor = when {
        approval.isApproved -> MaxmarColors.Success
        approval.isPendingApproval -> MaxmarColors.Warning
        else -> appColors.textSecondary
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = appColors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Employee + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaxmarColors.Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaxmarColors.Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = approval.employee?.name ?: "Unknown",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = appColors.textPrimary
                        )
                        Text(
                            text = approval.employee?.code ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = appColors.textSecondary
                        )
                    }
                }
                
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (approval.isApproved) 
                            Icons.Default.CheckCircle 
                        else if (approval.isPendingApproval) 
                            Icons.Default.Visibility 
                        else 
                            Icons.Default.Pending,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = approval.statusDisplay,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Type + Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = approval.type?.name ?: "-",
                    style = MaterialTheme.typography.bodyLarge,
                    color = appColors.textPrimary
                )
                Text(
                    text = approval.dateDisplay,
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textSecondary
                )
            }
            
            // Notes
            if (!approval.notes.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = approval.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = appColors.textSecondary
                )
            }
            
            // Action buttons (for pending items only)
            if (approval.isPendingAcknowledgement || approval.isPendingApproval) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Reject button
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaxmarColors.Error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tolak")
                    }
                    
                    // Acknowledge or Approve button
                    Button(
                        onClick = if (approval.isPendingAcknowledgement) onAcknowledge else onApprove,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (approval.isPendingAcknowledgement) 
                                MaxmarColors.Primary 
                            else 
                                MaxmarColors.Success
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (approval.isPendingAcknowledgement) "Ketahui" else "Setujui"
                        )
                    }
                }
            }
            
            // Approval info (for processed items)
            if (approval.isApproved) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Diketahui oleh",
                            style = MaterialTheme.typography.labelSmall,
                            color = appColors.textSecondary
                        )
                        Text(
                            text = approval.acknowledgedBy ?: "-",
                            style = MaterialTheme.typography.bodySmall,
                            color = appColors.textPrimary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Disetujui oleh",
                            style = MaterialTheme.typography.labelSmall,
                            color = appColors.textSecondary
                        )
                        Text(
                            text = approval.approvedBy ?: "-",
                            style = MaterialTheme.typography.bodySmall,
                            color = appColors.textPrimary
                        )
                    }
                }
            }
        }
    }
}
