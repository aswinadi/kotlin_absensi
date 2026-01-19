package com.maxmar.attendance.ui.screens.auth

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxmar.attendance.R
import com.maxmar.attendance.ui.theme.LocalAppColors
import com.maxmar.attendance.ui.theme.MaxmarColors

/**
 * Login screen with username/password authentication.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val appColors = LocalAppColors.current
    
    // Form state
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var rememberMe by rememberSaveable { mutableStateOf(false) }
    
    // Validation state
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    val isLoading = authState is AuthState.Loading
    
    // Handle auth state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                onLoginSuccess()
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar((authState as AuthState.Error).message)
                viewModel.clearError()
            }
            else -> {}
        }
    }
    
    fun validateAndLogin() {
        // Reset errors
        usernameError = null
        passwordError = null
        
        // Validate username
        if (username.isBlank()) {
            usernameError = "Username tidak boleh kosong"
            return
        }
        
        // Validate password
        if (password.isBlank()) {
            passwordError = "Password tidak boleh kosong"
            return
        }
        
        // Proceed with login
        focusManager.clearFocus()
        viewModel.login(username, password)
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                
                // Logo
                LogoSection()
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Header
                HeaderSection()
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Login Form
                LoginForm(
                    username = username,
                    onUsernameChange = { 
                        username = it
                        usernameError = null
                    },
                    usernameError = usernameError,
                    password = password,
                    onPasswordChange = { 
                        password = it
                        passwordError = null
                    },
                    passwordError = passwordError,
                    isPasswordVisible = isPasswordVisible,
                    onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                    onUsernameDone = { focusManager.moveFocus(FocusDirection.Down) },
                    onPasswordDone = { validateAndLogin() }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Remember me & Forgot password
                RememberMeAndForgotSection(
                    rememberMe = rememberMe,
                    onRememberMeChange = { rememberMe = it }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Login Button
                LoginButton(
                    isLoading = isLoading,
                    onClick = { validateAndLogin() }
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Footer
                FooterSection()
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun LogoSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.maxmar_logo),
            contentDescription = "Maxmar Logo",
            modifier = Modifier.size(150.dp)
        )
    }
}

@Composable
private fun HeaderSection() {
    val appColors = LocalAppColors.current
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Selamat Datang",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = appColors.textPrimary
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Masuk untuk melanjutkan",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = appColors.textSecondary
            )
        )
    }
}

@Composable
private fun LoginForm(
    username: String,
    onUsernameChange: (String) -> Unit,
    usernameError: String?,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String?,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    onUsernameDone: () -> Unit,
    onPasswordDone: () -> Unit
) {
    val appColors = LocalAppColors.current
    
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = appColors.textPrimary,
        unfocusedTextColor = appColors.textPrimary,
        focusedContainerColor = appColors.surface,
        unfocusedContainerColor = appColors.surface,
        focusedBorderColor = MaxmarColors.Primary,
        unfocusedBorderColor = appColors.textTertiary.copy(alpha = 0.5f),
        errorBorderColor = MaxmarColors.Error,
        cursorColor = MaxmarColors.Primary,
        focusedLabelColor = MaxmarColors.Primary,
        unfocusedLabelColor = appColors.textSecondary,
        focusedLeadingIconColor = appColors.textSecondary,
        unfocusedLeadingIconColor = appColors.textSecondary,
        focusedTrailingIconColor = appColors.textSecondary,
        unfocusedTrailingIconColor = appColors.textSecondary
    )
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // Username field
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            placeholder = { Text("Masukkan username") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null
                )
            },
            isError = usernameError != null,
            supportingText = usernameError?.let {
                { Text(it, color = MaxmarColors.Error) }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { onUsernameDone() }),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            placeholder = { Text("Masukkan password") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible) 
                            Icons.Outlined.VisibilityOff 
                        else 
                            Icons.Outlined.Visibility,
                        contentDescription = if (isPasswordVisible) 
                            "Hide password" 
                        else 
                            "Show password"
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) 
                VisualTransformation.None 
            else 
                PasswordVisualTransformation(),
            isError = passwordError != null,
            supportingText = passwordError?.let {
                { Text(it, color = MaxmarColors.Error) }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onPasswordDone() }),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RememberMeAndForgotSection(
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit
) {
    val appColors = LocalAppColors.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = onRememberMeChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaxmarColors.Primary,
                    uncheckedColor = appColors.textSecondary,
                    checkmarkColor = Color.White
                ),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Ingat saya",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = appColors.textSecondary
                )
            )
        }
        
        TextButton(onClick = { /* TODO: Forgot password */ }) {
            Text(
                text = "Lupa Password?",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaxmarColors.Primary,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaxmarColors.Primary,
            contentColor = Color.White,
            disabledContainerColor = MaxmarColors.Primary.copy(alpha = 0.6f),
            disabledContentColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = "Masuk",
                style = MaterialTheme.typography.labelLarge.copy(
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

@Composable
private fun FooterSection() {
    val appColors = LocalAppColors.current
    val appName = androidx.compose.ui.res.stringResource(id = R.string.app_name)
    val versionName = com.maxmar.attendance.BuildConfig.VERSION_NAME
    val buildType = if (com.maxmar.attendance.BuildConfig.DEBUG) "Debug" else "Release"
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = appColors.textTertiary.copy(alpha = 0.3f)
            )
            
            Text(
                text = appName,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = appColors.textTertiary
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = appColors.textTertiary.copy(alpha = 0.3f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "v$versionName ($buildType)",
            style = MaterialTheme.typography.bodySmall.copy(
                color = appColors.textTertiary
            )
        )
    }
}

