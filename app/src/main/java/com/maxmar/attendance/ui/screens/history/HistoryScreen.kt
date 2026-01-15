package com.maxmar.attendance.ui.screens.history

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Sick
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxmar.attendance.data.model.AbsentAttendance
import com.maxmar.attendance.data.model.Attendance
import com.maxmar.attendance.data.model.AttendanceSummary
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.maxmar.attendance.util.TimeUtils

/**
 * History screen showing attendance records.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.historyState.collectAsState()
    val listState = rememberLazyListState()
    val appColors = LocalAppColors.current
    
    // Detect when user scrolls near the end (only for attendance tab)
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= totalItems - 3 && totalItems > 0 && state.selectedTab == 0
        }
    }
    
    // Load more when scrolled near end
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && state.hasMore && !state.isLoadingMore) {
            viewModel.loadMore()
        }
    }

    // Initial load of absents and summary
    LaunchedEffect(Unit) {
        viewModel.loadAbsents()
        viewModel.loadSummary()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Riwayat Kehadiran",
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
                    containerColor = Color.Transparent,
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
            Column(modifier = Modifier.fillMaxSize()) {
                // Month Selector
                var showMonthPicker by remember { mutableStateOf(false) }
                
                MonthYearSelector(
                    currentMonth = state.selectedMonth,
                    currentYear = state.selectedYear,
                    onClick = { showMonthPicker = true }
                )
                
                if (showMonthPicker) {
                    ModalBottomSheet(
                        onDismissRequest = { showMonthPicker = false },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        containerColor = LocalAppColors.current.surface,
                        contentColor = LocalAppColors.current.textPrimary
                    ) {
                        MonthYearScrollablePicker(
                            currentMonth = state.selectedMonth,
                            currentYear = state.selectedYear,
                            onMonthSelected = { year, month ->
                                viewModel.setMonthYear(year, month)
                                showMonthPicker = false
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tabs
                val tabs = listOf("Kehadiran", "Izin/Cuti", "Ringkasan")
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
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = state.selectedTab == index,
                            onClick = { viewModel.setTab(index) },
                            text = { 
                                Text(
                                    text = title,
                                    fontWeight = if (state.selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                ) 
                            },
                            selectedContentColor = MaxmarColors.Primary,
                            unselectedContentColor = appColors.textSecondary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Content
                when (state.selectedTab) {
                    0 -> { // Attendance List
                        if (state.isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MaxmarColors.Primary)
                            }
                        } else if (state.error != null) {
                            ErrorContent(
                                message = state.error!!,
                                onRetry = { viewModel.loadHistory() },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (state.attendances.isEmpty()) {
                            EmptyContent(modifier = Modifier.fillMaxSize(), message = "Belum ada riwayat kehadiran")
                        } else {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item { Spacer(modifier = Modifier.height(8.dp)) }
                                
                                items(state.attendances, key = { it.id }) { attendance ->
                                    AttendanceCard(attendance = attendance)
                                }
                                
                                if (state.isLoadingMore) {
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
                                
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                            }
                        }
                    }
                    1 -> { // Absent List
                        if (state.isLoadingAbsents) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MaxmarColors.Primary)
                            }
                        } else if (state.absents.isEmpty()) {
                            EmptyContent(modifier = Modifier.fillMaxSize(), message = "Tidak ada data izin/cuti")
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item { Spacer(modifier = Modifier.height(8.dp)) }
                                
                                items(state.absents, key = { it.id }) { absent ->
                                    AbsentCard(absent = absent)
                                }
                                
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                            }
                        }
                    }
                    2 -> { // Summary
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (state.summaryError != null) {
                                ErrorContent(
                                    message = state.summaryError!!,
                                    onRetry = { viewModel.loadSummary() }
                                )
                            } else {
                                MonthlySummaryCard(summary = state.summary)
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceCard(attendance: Attendance) {
    val appColors = LocalAppColors.current
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("id"))
    val displayDate = try {
        LocalDate.parse(attendance.date).format(dateFormatter)
    } catch (e: Exception) {
        attendance.date
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
            // Date Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MaxmarColors.Primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = displayDate,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = appColors.textPrimary
                )
            }
            
            // Office
            attendance.office?.let { office ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = office,
                    style = MaterialTheme.typography.bodySmall,
                    color = appColors.textSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Check In / Check Out Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Check In
                TimeColumn(
                    label = "Check In",
                    time = attendance.checkIn?.time,
                    isWithinRadius = attendance.checkIn?.isWithinRadius,
                    icon = Icons.Default.Login,
                    color = MaxmarColors.CheckIn
                )
                
                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(50.dp)
                        .background(appColors.surfaceVariant)
                )
                
                // Check Out
                TimeColumn(
                    label = "Check Out",
                    time = attendance.checkOut?.time,
                    isWithinRadius = attendance.checkOut?.isWithinRadius,
                    icon = Icons.Default.Logout,
                    color = MaxmarColors.CheckOut
                )
            }
        }
    }
}

@Composable
private fun TimeColumn(
    label: String,
    time: String?,
    isWithinRadius: Boolean?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    val appColors = LocalAppColors.current
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = appColors.textSecondary
                )
                // Convert UTC time to local timezone
                val localTime = TimeUtils.convertUtcToLocalShort(time)
                Text(
                    text = localTime ?: "--:--",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (time != null) appColors.textPrimary else appColors.textTertiary
                )
            }
        }
        
        // Radius indicator
        if (time != null && isWithinRadius != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (isWithinRadius) MaxmarColors.Success else MaxmarColors.Warning,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isWithinRadius) "Dalam radius" else "Luar radius",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isWithinRadius) MaxmarColors.Success else MaxmarColors.Warning
                )
            }
        }
    }
}

@Composable
private fun EmptyContent(
    modifier: Modifier = Modifier,
    message: String = "Belum ada riwayat kehadiran"
) {
    val appColors = LocalAppColors.current
    
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = appColors.textTertiary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = appColors.textSecondary
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = MaxmarColors.Primary)
        ) {
            Text("Coba Lagi")
        }
    }
}


@Composable
private fun MonthYearSelector(
    currentMonth: Int,
    currentYear: Int,
    onClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    val monthName = remember(currentMonth) {
        val months = arrayOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )
        if (currentMonth in 1..12) months[currentMonth - 1] else ""
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            onClick = onClick,
            colors = CardDefaults.cardColors(
                containerColor = appColors.surface.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$monthName $currentYear",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = appColors.textPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select Month",
                    tint = appColors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun MonthYearScrollablePicker(
    currentMonth: Int,
    currentYear: Int,
    onMonthSelected: (Int, Int) -> Unit
) {
    val appColors = LocalAppColors.current
    val months = arrayOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )
    
    // Generate list of months (e.g., past 5 years to next 1 year)
    val startYear = LocalDate.now().year - 5
    val endYear = LocalDate.now().year + 1
    
    val monthList = remember {
        val list = mutableListOf<Pair<Int, Int>>() // Year, Month
        for (year in endYear downTo startYear) {
            for (month in 12 downTo 1) {
                list.add(year to month)
            }
        }
        list
    }
    
    val listState = rememberLazyListState()
    
    // Scroll to current selection on first open
    LaunchedEffect(Unit) {
        val index = monthList.indexOfFirst { it.first == currentYear && it.second == currentMonth }
        if (index != -1) {
            listState.scrollToItem(index)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // Fixed height for bottom sheet content
    ) {
        Text(
            text = "Pilih Bulan",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = appColors.textPrimary,
            modifier = Modifier.padding(16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)
        ) {
            items(monthList) { (year, month) ->
                val isSelected = year == currentYear && month == currentMonth
                val monthName = months[month - 1]
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isSelected) MaxmarColors.Primary.copy(alpha = 0.1f) else Color.Transparent)
                        .clickable { onMonthSelected(year, month) }
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$monthName $year",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) MaxmarColors.Primary else appColors.textPrimary
                    )
                    
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = MaxmarColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AbsentCard(absent: AbsentAttendance) {
    val appColors = LocalAppColors.current
    val displayDate = remember(absent.date) {
        try {
            val date = LocalDate.parse(absent.date)
            date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("id", "ID")))
        } catch (e: Exception) {
            absent.date
        }
    }
    
    val statusColor = when(absent.status) {
        "approved" -> MaxmarColors.Success
        "rejected" -> MaxmarColors.Error
        else -> MaxmarColors.Warning
    }
    
    val statusText = when(absent.status) {
        "approved" -> "Disetujui"
        "rejected" -> "Ditolak"
        "pending_approval" -> "Menunggu Persetujuan"
        else -> "Menunggu Konfirmasi"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = appColors.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Date & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = MaxmarColors.Error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = displayDate,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = appColors.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Type & Details
            Row(modifier = Modifier.fillMaxWidth()) {
                // Type Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            try {
                                Color(android.graphics.Color.parseColor(absent.type?.color ?: "#CCCCCC")).copy(alpha = 0.1f)
                            } catch (e: Exception) {
                                Color.Gray.copy(alpha = 0.1f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                   Icon(
                        imageVector = if (absent.type?.name?.contains("Sakit", ignoreCase = true) == true) 
                            Icons.Default.Sick else Icons.Default.BeachAccess,
                        contentDescription = null,
                        tint = try {
                                Color(android.graphics.Color.parseColor(absent.type?.color ?: "#CCCCCC"))
                            } catch (e: Exception) {
                                Color.Gray
                            },
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = absent.type?.name ?: "Izin",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = appColors.textPrimary
                    )
                    if (!absent.notes.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = absent.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = appColors.textSecondary,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthlySummaryCard(summary: AttendanceSummary?) {
    val appColors = LocalAppColors.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = appColors.surface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Ringkasan ${summary?.monthName ?: "Bulan Ini"}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = appColors.textPrimary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // First row: Hadir, Terlambat, Tidak Hadir
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(
                    label = "Hadir",
                    value = summary?.present?.toString() ?: "-",
                    color = MaxmarColors.CheckIn
                )
                StatItem(
                    label = "Terlambat",
                    value = summary?.late?.toString() ?: "-",
                    color = MaxmarColors.Warning
                )
                StatItem(
                    label = "Tidak Hadir",
                    value = summary?.absent?.toString() ?: "-",
                    color = MaxmarColors.CheckOut
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Second row: Sakit, Cuti
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Sakit",
                    value = summary?.sick?.toString() ?: "-",
                    color = MaxmarColors.Error
                )
                StatItem(
                    label = "Cuti",
                    value = summary?.leave?.toString() ?: "-",
                    color = MaxmarColors.Primary
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = color
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
    }
}
