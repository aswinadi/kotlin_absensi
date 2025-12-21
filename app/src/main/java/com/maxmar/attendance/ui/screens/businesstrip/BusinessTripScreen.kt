package com.maxmar.attendance.ui.screens.businesstrip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxmar.attendance.data.model.BusinessTrip
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors

/**
 * Business Trip list screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessTripScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToDetail: (Int) -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    viewModel: BusinessTripViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsState()
    val appColors = LocalAppColors.current
    val listState = rememberLazyListState()
    
    // Pagination trigger
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= state.trips.size - 3 && state.hasMore && !state.isLoading
        }
    }
    
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadMore()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perjalanan Dinas", fontWeight = FontWeight.Bold) },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaxmarColors.Primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Buat Perjalanan Dinas"
                )
            }
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
                onFilterSelected = { viewModel.setFilter(it) }
            )
            
            // List
            if (state.isLoading && state.trips.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaxmarColors.Primary)
                }
            } else if (state.trips.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = appColors.textSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Belum ada perjalanan dinas",
                            color = appColors.textSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.trips, key = { it.id }) { trip ->
                        BusinessTripCard(
                            trip = trip,
                            onClick = { onNavigateToDetail(trip.id) }
                        )
                    }
                    
                    if (state.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaxmarColors.Primary,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChipsRow(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val appColors = LocalAppColors.current
    val filters = listOf(
        "all" to "Semua",
        "pending" to "Menunggu",
        "approved" to "Disetujui"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (value, label) ->
            FilterChip(
                selected = selectedFilter == value,
                onClick = { onFilterSelected(value) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaxmarColors.Primary,
                    selectedLabelColor = Color.White,
                    containerColor = appColors.cardBackground,
                    labelColor = appColors.textSecondary
                )
            )
        }
    }
}

@Composable
private fun BusinessTripCard(
    trip: BusinessTrip,
    onClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    
    val statusColor = when (trip.status.lowercase()) {
        "approved" -> MaxmarColors.Success
        "pending" -> MaxmarColors.Warning
        "rejected" -> MaxmarColors.Error
        else -> appColors.textSecondary
    }
    
    val statusText = when (trip.status.lowercase()) {
        "approved" -> "Disetujui"
        "pending" -> "Menunggu"
        "rejected" -> "Ditolak"
        else -> trip.status
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = appColors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Code + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trip.transactionCode,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = appColors.textPrimary
                )
                
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (trip.status.lowercase() == "approved") 
                            Icons.Default.CheckCircle else Icons.Default.Pending,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Purpose
            Text(
                text = trip.purpose,
                style = MaterialTheme.typography.bodyLarge,
                color = appColors.textPrimary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaxmarColors.Primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = trip.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Dates
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaxmarColors.Primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${trip.startDate} - ${trip.endDate} (${trip.days} hari)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textSecondary
                )
            }
        }
    }
}
