package com.maxmar.attendance.ui.screens.home

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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxmar.attendance.data.model.AttendanceSummary
import com.maxmar.attendance.data.model.Shift
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors

/**
 * Home screen with dashboard, attendance actions, and navigation.
 */
@Composable
fun HomeScreen(
    onNavigateToHistory: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToAbsent: () -> Unit = {},
    onNavigateToCheckIn: () -> Unit = {},
    onNavigateToCheckOut: () -> Unit = {},
    onNavigateToBusinessTrip: () -> Unit = {},
    onNavigateToApproval: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.homeState.collectAsState()
    // Always reset to Home (index 0) when this screen is displayed
    var selectedNavIndex by rememberSaveable { mutableIntStateOf(0) }
    val appColors = LocalAppColors.current
    
    // Reset to Home tab when screen recomposes (coming back from other screens)
    LaunchedEffect(Unit) {
        selectedNavIndex = 0
    }
    
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedIndex = selectedNavIndex,
                onItemSelected = { index ->
                    when (index) {
                        0 -> selectedNavIndex = 0 // Stay on home
                        1 -> onNavigateToHistory()
                        2 -> onNavigateToApproval()
                        3 -> onNavigateToProfile()
                    }
                }
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
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaxmarColors.Primary
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    // Header
                    HeaderSection(
                        greeting = viewModel.getGreeting(),
                        employeeName = state.employee?.fullName ?: "User",
                        notificationCount = state.unreadNotificationCount,
                        onNotificationClick = onNavigateToNotifications
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Today's Shift Card
                    ShiftCard(
                        isWorkday = state.isWorkday,
                        shift = state.shift
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Today's Attendance Card
                    TodayAttendanceCard(
                        hasCheckedIn = state.hasCheckedIn,
                        hasCheckedOut = state.hasCheckedOut,
                        checkInTime = state.checkInTime,
                        checkOutTime = state.checkOutTime
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Action Buttons
                    ActionButtonsRow(
                        hasCheckedIn = state.hasCheckedIn,
                        hasCheckedOut = state.hasCheckedOut,
                        onCheckIn = onNavigateToCheckIn,
                        onCheckOut = onNavigateToCheckOut,
                        onAbsent = onNavigateToAbsent,
                        onBusinessTrip = onNavigateToBusinessTrip,
                        onApproval = onNavigateToApproval
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Monthly Summary
                    MonthlySummaryCard(summary = state.summary)
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(
    greeting: String,
    employeeName: String,
    notificationCount: Int,
    onNotificationClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaxmarColors.Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaxmarColors.Primary,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Greeting
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$greeting,",
                style = MaterialTheme.typography.bodyMedium,
                color = appColors.textSecondary
            )
            Text(
                text = employeeName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = appColors.textPrimary
            )
        }
        
        // Notification Bell
        BadgedBox(
            badge = {
                if (notificationCount > 0) {
                    Badge(containerColor = MaxmarColors.Error) {
                        Text(notificationCount.toString(), color = Color.White)
                    }
                }
            }
        ) {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = appColors.textSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun ShiftCard(
    isWorkday: Boolean,
    shift: Shift?
) {
    val appColors = LocalAppColors.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isWorkday) MaxmarColors.Primary else appColors.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isWorkday) Color.White.copy(alpha = 0.2f)
                        else MaxmarColors.Primary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isWorkday) Icons.Default.Schedule else Icons.Default.Weekend,
                    contentDescription = null,
                    tint = if (isWorkday) Color.White else appColors.textSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isWorkday) "Jadwal Hari Ini" else "Hari Libur",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isWorkday) Color.White.copy(alpha = 0.7f) else appColors.textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isWorkday && shift != null) {
                        "${shift.startTime} - ${shift.endTime}"
                    } else {
                        "Tidak ada jadwal kerja"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isWorkday) Color.White else appColors.textPrimary
                )
            }
            
            if (isWorkday && shift?.dayLabel != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = shift.dayLabel,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayAttendanceCard(
    hasCheckedIn: Boolean,
    hasCheckedOut: Boolean,
    checkInTime: String?,
    checkOutTime: String?
) {
    val appColors = LocalAppColors.current
    val currentDate = remember {
        java.text.SimpleDateFormat("EEEE, dd MMMM yyyy", java.util.Locale("id", "ID"))
            .format(java.util.Date())
    }
    
    // Auto-updating time every second
    var currentTime by remember { mutableStateOf(
        java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date())
    )}
    
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date())
            kotlinx.coroutines.delay(1000) // Check every second
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = appColors.cardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Date and Time Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = currentDate,
                        style = MaterialTheme.typography.titleSmall,
                        color = appColors.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaxmarColors.Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = currentTime,
                        style = MaterialTheme.typography.bodyMedium,
                        color = appColors.textSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Check In / Check Out Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Check In
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (hasCheckedIn) MaxmarColors.CheckIn else appColors.textSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Check In",
                        style = MaterialTheme.typography.labelSmall,
                        color = appColors.textSecondary
                    )
                    Text(
                        text = checkInTime ?: "--:--",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (hasCheckedIn) MaxmarColors.CheckIn else appColors.textSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(60.dp)
                        .background(appColors.textSecondary.copy(alpha = 0.2f))
                )
                
                // Check Out
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        tint = if (hasCheckedOut) MaxmarColors.CheckOut else appColors.textSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Check Out",
                        style = MaterialTheme.typography.labelSmall,
                        color = appColors.textSecondary
                    )
                    Text(
                        text = checkOutTime ?: "--:--",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (hasCheckedOut) MaxmarColors.CheckOut else appColors.textSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButtonsRow(
    hasCheckedIn: Boolean,
    hasCheckedOut: Boolean,
    onCheckIn: () -> Unit,
    onCheckOut: () -> Unit,
    onAbsent: () -> Unit,
    onBusinessTrip: () -> Unit,
    onApproval: () -> Unit // Keep param for compatibility but don't use
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // First row: Check In, Check Out
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(
                label = "Check In",
                icon = Icons.Default.CheckCircle,
                color = MaxmarColors.CheckIn,
                enabled = !hasCheckedIn,
                onClick = onCheckIn,
                modifier = Modifier.weight(1f)
            )
            
            ActionButton(
                label = "Check Out",
                icon = Icons.Default.Logout,
                color = MaxmarColors.CheckOut,
                enabled = hasCheckedIn && !hasCheckedOut,
                onClick = onCheckOut,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Second row: Izin, Perdin
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(
                label = "Izin",
                icon = Icons.Default.EventBusy,
                color = MaxmarColors.Absent,
                enabled = true,
                onClick = onAbsent,
                modifier = Modifier.weight(1f)
            )
            
            ActionButton(
                label = "Perdin",
                icon = Icons.Default.FlightTakeoff,
                color = MaxmarColors.Primary,
                enabled = true,
                onClick = onBusinessTrip,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val displayColor = if (enabled) color else appColors.textTertiary.copy(alpha = 0.3f)
    
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(enabled = enabled, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = appColors.surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(displayColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = displayColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (enabled) appColors.textPrimary else appColors.textTertiary
            )
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

@Composable
private fun BottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val appColors = LocalAppColors.current
    
    NavigationBar(
        containerColor = appColors.surface,
        contentColor = appColors.textPrimary
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedIndex == 0,
            onClick = { onItemSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaxmarColors.Primary,
                selectedTextColor = MaxmarColors.Primary,
                indicatorColor = MaxmarColors.Primary.copy(alpha = 0.1f),
                unselectedIconColor = appColors.textSecondary,
                unselectedTextColor = appColors.textSecondary
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.History, contentDescription = "History") },
            label = { Text("History") },
            selected = selectedIndex == 1,
            onClick = { onItemSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaxmarColors.Primary,
                selectedTextColor = MaxmarColors.Primary,
                indicatorColor = MaxmarColors.Primary.copy(alpha = 0.1f),
                unselectedIconColor = appColors.textSecondary,
                unselectedTextColor = appColors.textSecondary
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Approval, contentDescription = "Approval") },
            label = { Text("Approval") },
            selected = selectedIndex == 2,
            onClick = { onItemSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaxmarColors.Primary,
                selectedTextColor = MaxmarColors.Primary,
                indicatorColor = MaxmarColors.Primary.copy(alpha = 0.1f),
                unselectedIconColor = appColors.textSecondary,
                unselectedTextColor = appColors.textSecondary
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = selectedIndex == 3,
            onClick = { onItemSelected(3) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaxmarColors.Primary,
                selectedTextColor = MaxmarColors.Primary,
                indicatorColor = MaxmarColors.Primary.copy(alpha = 0.1f),
                unselectedIconColor = appColors.textSecondary,
                unselectedTextColor = appColors.textSecondary
            )
        )
    }
}
