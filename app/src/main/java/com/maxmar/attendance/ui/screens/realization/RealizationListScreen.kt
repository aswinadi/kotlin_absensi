package com.maxmar.attendance.ui.screens.realization

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
 * Screen showing trips that need realization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealizationListScreen(
    onNavigateBack: () -> Unit = {},
    onTripSelected: (Int) -> Unit = {},
    viewModel: BusinessTripRealizationViewModel = hiltViewModel()
) {
    val listState by viewModel.listState.collectAsState()
    val appColors = LocalAppColors.current
    
    LaunchedEffect(Unit) {
        viewModel.loadTripsNeedingRealization()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Realisasi Perdin", fontWeight = FontWeight.Bold) },
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
                listState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaxmarColors.Primary)
                    }
                }
                listState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = listState.error ?: "Terjadi kesalahan",
                                color = appColors.textSecondary
                            )
                        }
                    }
                }
                listState.tripsNeedingRealization.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = appColors.textSecondary,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Tidak ada perdin yang perlu direalisasi",
                                color = appColors.textSecondary
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(listState.tripsNeedingRealization) { trip ->
                            TripNeedingRealizationCard(
                                trip = trip,
                                onClick = { onTripSelected(trip.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TripNeedingRealizationCard(
    trip: BusinessTrip,
    onClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = appColors.cardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trip.transactionCode,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = appColors.textPrimary
                )
                Surface(
                    color = MaxmarColors.Warning.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Perlu Realisasi",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaxmarColors.Warning
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Purpose
            trip.purpose?.let { purposeText ->
                Text(
                    text = purposeText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = appColors.textPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = appColors.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${trip.location} - ${trip.destinationCity ?: ""}",
                    fontSize = 13.sp,
                    color = appColors.textSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Dates
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = appColors.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${trip.startDate ?: ""} - ${trip.endDate ?: ""} (${trip.days} hari)",
                    fontSize = 13.sp,
                    color = appColors.textSecondary
                )
            }
            
            // Cash Advance if any
            if ((trip.cashAdvance ?: 0.0) > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Uang Muka:",
                        fontSize = 13.sp,
                        color = appColors.textSecondary
                    )
                    Text(
                        text = formatter.format(trip.cashAdvance ?: 0.0),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaxmarColors.Primary
                    )
                }
            }
        }
    }
}
