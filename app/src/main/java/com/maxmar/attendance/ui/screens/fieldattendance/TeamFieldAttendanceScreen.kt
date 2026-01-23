package com.maxmar.attendance.ui.screens.fieldattendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

/**
 * Screen for supervisors to view their team's field attendance.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamFieldAttendanceScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: TeamFieldAttendanceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val appColors = LocalAppColors.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tim Dinas Luar", fontWeight = FontWeight.Bold) },
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
            // Tab Row: Hari Ini / Rencana
            TabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaxmarColors.Primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[state.selectedTab]),
                        color = MaxmarColors.Primary
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = state.selectedTab == 0,
                    onClick = { viewModel.setTab(0) },
                    text = {
                        Text(
                            text = "Hari Ini",
                            fontWeight = if (state.selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    selectedContentColor = MaxmarColors.Primary,
                    unselectedContentColor = appColors.textSecondary
                )
                Tab(
                    selected = state.selectedTab == 1,
                    onClick = { viewModel.setTab(1) },
                    text = {
                        Text(
                            text = "Rencana",
                            fontWeight = if (state.selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    selectedContentColor = MaxmarColors.Primary,
                    unselectedContentColor = appColors.textSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Content
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaxmarColors.Primary)
                }
            } else if (state.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error ?: "Terjadi kesalahan",
                            color = appColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadData() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaxmarColors.Primary)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
            } else if (state.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.selectedTab == 0) 
                            "Tidak ada tim yang dinas luar hari ini" 
                        else 
                            "Tidak ada rencana dinas luar",
                        color = appColors.textSecondary
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.items, key = { it.id }) { item ->
                        TeamFieldAttendanceCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamFieldAttendanceCard(item: TeamFieldAttendanceItem) {
    val appColors = LocalAppColors.current
    
    val statusColor = when {
        item.hasArrived && item.hasDeparted -> MaxmarColors.Success
        item.hasArrived -> MaxmarColors.Primary
        else -> appColors.textSecondary
    }
    
    val statusText = when {
        item.hasArrived && item.hasDeparted -> "Selesai"
        item.hasArrived -> "Sudah Tiba"
        else -> "Belum Berangkat"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = appColors.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Employee info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaxmarColors.Primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaxmarColors.Primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.employeeName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = appColors.textPrimary
                    )
                    Text(
                        text = item.position ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = appColors.textSecondary
                    )
                }
                
                // Status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = statusColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
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
                    text = item.locationName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Purpose
            Text(
                text = item.purpose,
                style = MaterialTheme.typography.bodySmall,
                color = appColors.textSecondary
            )
            
            // Time info if available
            if (item.arrivalTime != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = appColors.textSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tiba: ${item.arrivalTime}${if (item.departureTime != null) " • Pulang: ${item.departureTime}" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = appColors.textSecondary
                    )
                }
            }
        }
    }
}
