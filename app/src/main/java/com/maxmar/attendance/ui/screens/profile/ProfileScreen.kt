package com.maxmar.attendance.ui.screens.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxmar.attendance.data.model.LeaveQuota
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors


/**
 * Profile screen showing employee information with logout option.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.profileState.collectAsState()
    val logoutEvent by viewModel.logoutEvent.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val appColors = LocalAppColors.current
    
    // Handle logout navigation
    LaunchedEffect(logoutEvent) {
        if (logoutEvent) {
            onLogout()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profil",
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
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaxmarColors.Primary
                )
            } else if (state.error != null) {
                ErrorContent(
                    message = state.error!!,
                    onRetry = { viewModel.loadProfile() },
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                ProfileContent(
                    state = state,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    onLogout = { viewModel.logout() }
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    state: ProfileState,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onLogout: () -> Unit
) {
    val appColors = LocalAppColors.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaxmarColors.Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaxmarColors.Primary,
                modifier = Modifier.size(56.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Employee Name
        Text(
            text = state.employee?.fullName ?: "Unknown",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = appColors.textPrimary
        )
        
        // Employee Code
        state.employee?.employeeCode?.let { code ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = code,
                style = MaterialTheme.typography.bodyMedium,
                color = appColors.textSecondary
            )
        }
        
        // Position
        state.employee?.position?.let { position ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = position,
                style = MaterialTheme.typography.bodyMedium,
                color = MaxmarColors.Primary
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Leave Quota Card
        state.leaveQuota?.let { quota ->
            LeaveQuotaCard(quota = quota)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Info Cards
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
                Text(
                    text = "Informasi Karyawan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = appColors.textPrimary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Employee Code
                state.employee?.employeeCode?.let {
                    ProfileInfoRow(
                        icon = Icons.Default.Badge,
                        label = "Kode Karyawan",
                        value = it
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Email
                state.employee?.email?.let {
                    ProfileInfoRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = it
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Phone
                state.employee?.phone?.let {
                    ProfileInfoRow(
                        icon = Icons.Default.Phone,
                        label = "Telepon",
                        value = it
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Department
                state.employee?.department?.let {
                    ProfileInfoRow(
                        icon = Icons.Default.Business,
                        label = "Departemen",
                        value = it
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Position
                state.employee?.position?.let {
                    ProfileInfoRow(
                        icon = Icons.Default.Work,
                        label = "Jabatan",
                        value = it
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Company
                state.employee?.company?.let {
                    ProfileInfoRow(
                        icon = Icons.Default.Business,
                        label = "Perusahaan",
                        value = it
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Office
                state.employee?.office?.name?.let {
                    ProfileInfoRow(
                        icon = Icons.Default.LocationOn,
                        label = "Kantor",
                        value = it
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Settings Card
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
                Text(
                    text = "Pengaturan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = appColors.textPrimary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Dark Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaxmarColors.Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Nightlight,
                            contentDescription = null,
                            tint = MaxmarColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mode Gelap",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = appColors.textPrimary
                        )
                        Text(
                            text = if (isDarkMode) "Aktif" else "Nonaktif",
                            style = MaterialTheme.typography.bodySmall,
                            color = appColors.textSecondary
                        )
                    }
                    
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onToggleDarkMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaxmarColors.Primary,
                            checkedTrackColor = MaxmarColors.Primary.copy(alpha = 0.3f),
                            uncheckedThumbColor = appColors.textSecondary,
                            uncheckedTrackColor = appColors.surfaceVariant
                        )
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaxmarColors.Error.copy(alpha = 0.1f),
                contentColor = MaxmarColors.Error
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = !state.isLoggingOut
        ) {
            if (state.isLoggingOut) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaxmarColors.Error,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Keluar",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // App Version
        Text(
            text = "Maxmar Attendance v1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = appColors.textTertiary
        )
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    val appColors = LocalAppColors.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaxmarColors.Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaxmarColors.Primary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = appColors.textSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = appColors.textPrimary
            )
        }
    }
}

@Composable
private fun LeaveQuotaCard(quota: LeaveQuota) {
    val appColors = LocalAppColors.current
    val total = quota.total ?: 0
    val remaining = quota.remaining ?: 0
    val used = quota.used ?: 0
    val progress = if (total > 0) remaining.toFloat() / total.toFloat() else 0f
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaxmarColors.Primary),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sisa Cuti Tahunan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White
                )
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${quota.year ?: "-"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "$remaining",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "dari $total hari",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Terpakai: $used hari",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
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
