package com.maxmar.attendance.ui.screens.businesstrip

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxmar.attendance.data.model.BusinessTrip
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors
import java.text.NumberFormat
import java.util.Locale

/**
 * Business Trip detail screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessTripDetailScreen(
    tripId: Int,
    onNavigateBack: () -> Unit = {},
    viewModel: BusinessTripViewModel = hiltViewModel()
) {
    val state by viewModel.detailState.collectAsState()
    val appColors = LocalAppColors.current
    
    LaunchedEffect(tripId) {
        viewModel.loadTripDetail(tripId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Perjalanan Dinas", fontWeight = FontWeight.Bold) },
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
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaxmarColors.Primary
                    )
                }
                state.error != null -> {
                    Text(
                        text = state.error ?: "Error",
                        color = MaxmarColors.Error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.trip != null -> {
                    TripDetailContent(trip = state.trip!!)
                }
            }
        }
    }
}

@Composable
private fun TripDetailContent(trip: BusinessTrip) {
    val appColors = LocalAppColors.current
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    val statusColor = when (trip.status.lowercase()) {
        "approved" -> MaxmarColors.Success
        "pending" -> MaxmarColors.Warning
        "rejected" -> MaxmarColors.Error
        else -> appColors.textSecondary
    }
    
    val statusText = when (trip.status.lowercase()) {
        "approved" -> "Disetujui"
        "pending" -> "Menunggu Persetujuan"
        "rejected" -> "Ditolak"
        else -> trip.status
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = appColors.cardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = trip.transactionCode,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = appColors.textPrimary
                    )
                    
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(statusColor.copy(alpha = 0.1f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (trip.status.lowercase() == "approved") 
                                Icons.Default.CheckCircle else Icons.Default.Pending,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = trip.purpose,
                    style = MaterialTheme.typography.titleMedium,
                    color = appColors.textPrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Trip Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = appColors.cardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Informasi Perjalanan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = appColors.textPrimary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                InfoRow(
                    icon = Icons.Default.LocationOn,
                    label = "Lokasi",
                    value = trip.location
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                InfoRow(
                    icon = Icons.Default.CalendarMonth,
                    label = "Tanggal",
                    value = "${trip.startDate} - ${trip.endDate}"
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                InfoRow(
                    icon = Icons.Default.AccessTime,
                    label = "Durasi",
                    value = "${trip.days} hari"
                )
                
                if (!trip.destinationCity.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(
                        icon = Icons.Default.LocationOn,
                        label = "Kota Tujuan",
                        value = trip.destinationCity
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Allowance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = appColors.cardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tunjangan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = appColors.textPrimary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                InfoRow(
                    icon = Icons.Default.Money,
                    label = "Per Hari",
                    value = currencyFormat.format(trip.allowancePerDay)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                HorizontalDivider(color = appColors.textSecondary.copy(alpha = 0.2f))
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Tunjangan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = appColors.textPrimary
                    )
                    Text(
                        text = currencyFormat.format(trip.totalAllowance),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaxmarColors.Success
                    )
                }
                
                if (trip.cashAdvance > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Uang Muka",
                            style = MaterialTheme.typography.bodyMedium,
                            color = appColors.textSecondary
                        )
                        Text(
                            text = currencyFormat.format(trip.cashAdvance),
                            style = MaterialTheme.typography.bodyMedium,
                            color = appColors.textPrimary
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Approval Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = appColors.cardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Status Persetujuan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = appColors.textPrimary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ApprovalRow(
                    label = "Ditugaskan oleh",
                    approver = trip.assignedBy?.by ?: "-"
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                ApprovalRow(
                    label = "Diketahui oleh",
                    approver = trip.acknowledgedBy?.by ?: "Menunggu"
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                ApprovalRow(
                    label = "Disetujui oleh",
                    approver = trip.approvedBy?.by ?: "Menunggu"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    val appColors = LocalAppColors.current
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaxmarColors.Primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = appColors.textSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = appColors.textPrimary
            )
        }
    }
}

@Composable
private fun ApprovalRow(
    label: String,
    approver: String
) {
    val appColors = LocalAppColors.current
    val isCompleted = approver != "Menunggu" && approver != "-"
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = if (isCompleted) MaxmarColors.Success else appColors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = appColors.textSecondary
            )
        }
        Text(
            text = approver,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isCompleted) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (isCompleted) appColors.textPrimary else appColors.textSecondary
        )
    }
}
