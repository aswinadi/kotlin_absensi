package com.maxmar.attendance.ui.screens.absent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxmar.attendance.ui.components.ApprovalTimeline
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors
import com.maxmar.attendance.util.DateTimeUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsentDetailScreen(
    absentId: Int,
    onNavigateBack: () -> Unit = {},
    viewModel: AbsentDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val appColors = LocalAppColors.current
    
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectReason by remember { mutableStateOf("") }
    
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(absentId) {
        viewModel.loadDetail(absentId)
    }
    
    LaunchedEffect(state.actionSuccess) {
        state.actionSuccess?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
// ... existing topBar ...
            TopAppBar(
                title = { Text("Detail Pengajuan", fontWeight = FontWeight.Bold) },
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
        Box(
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
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaxmarColors.Primary
                )
            } else if (state.error != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.error!!, color = appColors.textSecondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadDetail(absentId) }) {
                        Text("Coba Lagi")
                    }
                }
            } else if (state.absent != null) {
                val absent = state.absent!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Employee Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = appColors.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaxmarColors.Primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaxmarColors.Primary
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = absent.employee?.fullName ?: "Unknown",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = appColors.textPrimary
                                )
                                Text(
                                    text = absent.employee?.employeeCode ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = appColors.textSecondary
                                )
                            }
                        }
                    }

                    // Request Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = appColors.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            InfoRow(
                                icon = Icons.Default.Info,
                                label = "Jenis Pengajuan",
                                value = absent.type?.name ?: "-",
                                valueColor = MaxmarColors.Primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            InfoRow(
                                icon = Icons.Default.CalendarMonth,
                                label = "Tanggal",
                                value = DateTimeUtil.formatToDDMMYYYY(absent.date)
                            )
                            
                            if (!absent.notes.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Catatan:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = appColors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = absent.notes,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = appColors.textPrimary
                                )
                            }
                        }
                    }

                    // Approval Timeline Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = appColors.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Timeline Persetujuan",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = appColors.textPrimary,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            ApprovalTimeline(
                                requesterName = absent.employee?.fullName ?: "Karyawan",
                                requestDate = DateTimeUtil.formatToDDMMYYYY(absent.date),
                                ackInfo = absent.acknowledged,
                                appInfo = absent.approved
                            )
                        }
                    }

                    // Supervisor Actions
                    val userLevel = state.userPositionLevel ?: 99
                    val requesterLevel = absent.employee?.positionLevel ?: 99

                    val isLevel1Boss = userLevel == requesterLevel - 1
                    val isLevel2Boss = userLevel == requesterLevel - 2
                    
                    // Fallback case: if user is Level 1 boss and there is no Level 2 boss above them
                    // This is hard to detect perfectly on UI without more metadata, 
                    // but we can trust the backend filter to only show items that are 'their turn'.
                    
                    val showAcknowledge = isLevel1Boss && absent.isPendingAcknowledgement
                    val showApprove = (isLevel2Boss || isLevel1Boss) && absent.isPendingApproval

                    if (showAcknowledge) {
                        Button(
                            onClick = { viewModel.acknowledge(absent.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaxmarColors.Primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            } else {
                                Text("Acknowledge (Ketahui)")
                            }
                        }
                    } else if (showApprove) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showRejectDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaxmarColors.Error)
                            ) {
                                Text("Tolak")
                            }
                            Button(
                                onClick = { viewModel.approve(absent.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaxmarColors.Success)
                            ) {
                                if (state.isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                                } else {
                                    Text("Setujui")
                                }
                            }
                        }
                    }

                    if (showRejectDialog) {
                        // ... rejection dialog code remains same ...
                        AlertDialog(
                            onDismissRequest = { showRejectDialog = false },
                            title = { Text("Tolak Pengajuan") },
                            text = {
                                Column {
                                    Text("Berikan alasan penolakan:")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = rejectReason,
                                        onValueChange = { rejectReason = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = { Text("Alasan...") }
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.reject(absent.id, rejectReason)
                                        showRejectDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaxmarColors.Error)
                                ) {
                                    Text("Tolak")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showRejectDialog = false }) {
                                    Text("Batal")
                                }
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color = LocalAppColors.current.textPrimary
) {
    val appColors = LocalAppColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = appColors.textSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = appColors.textSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = valueColor
            )
        }
    }
}
