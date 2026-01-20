package com.maxmar.attendance.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxmar.attendance.data.model.Employee
import com.maxmar.attendance.data.model.UpdateProfileRequest
import com.maxmar.attendance.ui.components.LoadingOverlay
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(
    onProfileCompleted: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val appColors = LocalAppColors.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val authState by viewModel.authState.collectAsState()
    
    // We assume checkAuthStatus has populated the employee data in some way, 
    // or we fetch it here. For now, let's assume we might have some initial data if available.
    // Ideally, AuthViewModel should hold temporary employee data or fetch it.
    
    // Form fields
    var phone by remember { mutableStateOf("") }
    var nik by remember { mutableStateOf("") }
    var permanentAddress by remember { mutableStateOf("") }
    var permanentCity by remember { mutableStateOf("") }
    var currentAddress by remember { mutableStateOf("") }
    var currentCity by remember { mutableStateOf("") }
    var sameAsPermanent by remember { mutableStateOf(false) }
    
    val isLoading = authState is AuthState.Loading
    
    LaunchedEffect(Unit) {
        viewModel.fetchProfileForCompletion(
            onSuccess = { employee ->
                phone = employee.phone ?: ""
                nik = employee.nik ?: ""
                permanentAddress = employee.permanentAddress ?: ""
                permanentCity = employee.permanentCity ?: ""
                currentAddress = employee.currentAddress ?: ""
                currentCity = employee.currentCity ?: ""
            },
            onError = { msg ->
                scope.launch { snackbarHostState.showSnackbar(msg) }
            }
        )
    }

    // Auto-copy address logic
    LaunchedEffect(sameAsPermanent, permanentAddress, permanentCity) {
        if (sameAsPermanent) {
            currentAddress = permanentAddress
            currentCity = permanentCity
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lengkapi Profil") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appColors.surface,
                    titleContentColor = appColors.textPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Mohon lengkapi data diri Anda sebelum melanjutkan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textSecondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("No. HP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                    )
                )

                OutlinedTextField(
                    value = nik,
                    onValueChange = { if (it.length <= 16) nik = it },
                    label = { Text("NIK (16 digit)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                    )
                )

                Text(
                    text = "Alamat Tetap (Sesuai KTP)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = appColors.textPrimary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp).align(Alignment.Start)
                )

                OutlinedTextField(
                    value = permanentAddress,
                    onValueChange = { permanentAddress = it },
                    label = { Text("Alamat Lengkap") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                    )
                )

                OutlinedTextField(
                    value = permanentCity,
                    onValueChange = { permanentCity = it },
                    label = { Text("Kota") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                    )
                )

                Text(
                    text = "Alamat Sekarang (Korespondensi)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = appColors.textPrimary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp).align(Alignment.Start)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = sameAsPermanent,
                        onCheckedChange = { sameAsPermanent = it }
                    )
                    Text(
                        text = "Sama dengan Alamat Tetap",
                        style = MaterialTheme.typography.bodyMedium,
                        color = appColors.textPrimary
                    )
                }

                OutlinedTextField(
                    value = currentAddress,
                    onValueChange = { if (!sameAsPermanent) currentAddress = it },
                    label = { Text("Alamat Lengkap") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    minLines = 2,
                    enabled = !sameAsPermanent,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                        disabledTextColor = appColors.textSecondary,
                    )
                )

                OutlinedTextField(
                    value = currentCity,
                    onValueChange = { if (!sameAsPermanent) currentCity = it },
                    label = { Text("Kota") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    enabled = !sameAsPermanent,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                        disabledTextColor = appColors.textSecondary,
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (phone.isBlank() || nik.isBlank() || permanentAddress.isBlank() || 
                            permanentCity.isBlank() || currentAddress.isBlank() || currentCity.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Mohon lengkapi semua data") }
                            return@Button
                        }
                        
                        if (nik.length != 16) {
                            scope.launch { snackbarHostState.showSnackbar("NIK harus 16 digit") }
                            return@Button
                        }

                        val request = UpdateProfileRequest(
                            phone = phone,
                            nik = nik,
                            permanentAddress = permanentAddress,
                            permanentCity = permanentCity,
                            currentAddress = currentAddress,
                            currentCity = currentCity
                        )

                        viewModel.completeProfile(
                            request,
                            onSuccess = {
                                onProfileCompleted()
                            },
                            onError = { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaxmarColors.Primary),
                    enabled = !isLoading
                ) {
                    Text("Simpan dan Lanjutkan")
                }
            }
            
            if (isLoading) {
                LoadingOverlay()
            }
        }
    }
}
