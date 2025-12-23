package com.maxmar.attendance.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Dark color scheme for Material 3
 */
private val DarkColorScheme = darkColorScheme(
    primary = MaxmarColors.Primary,
    onPrimary = DarkColors.TextPrimary,
    primaryContainer = MaxmarColors.PrimaryDark,
    onPrimaryContainer = DarkColors.TextPrimary,
    
    secondary = MaxmarColors.Primary,
    onSecondary = DarkColors.TextPrimary,
    
    tertiary = MaxmarColors.CheckIn,
    onTertiary = DarkColors.TextPrimary,
    
    background = DarkColors.Background,
    onBackground = DarkColors.OnBackground,
    
    surface = DarkColors.Surface,
    onSurface = DarkColors.OnSurface,
    surfaceVariant = DarkColors.SurfaceVariant,
    onSurfaceVariant = DarkColors.OnSurfaceVariant,
    
    error = MaxmarColors.Error,
    onError = DarkColors.TextPrimary
)

/**
 * Light color scheme for Material 3
 */
private val LightColorScheme = lightColorScheme(
    primary = MaxmarColors.Primary,
    onPrimary = LightColors.Surface,
    primaryContainer = MaxmarColors.PrimaryLight,
    onPrimaryContainer = LightColors.Surface,
    
    secondary = MaxmarColors.Primary,
    onSecondary = LightColors.Surface,
    
    tertiary = MaxmarColors.CheckIn,
    onTertiary = LightColors.Surface,
    
    background = LightColors.Background,
    onBackground = LightColors.OnBackground,
    
    surface = LightColors.Surface,
    onSurface = LightColors.OnSurface,
    surfaceVariant = LightColors.SurfaceVariant,
    onSurfaceVariant = LightColors.OnSurfaceVariant,
    
    error = MaxmarColors.Error,
    onError = LightColors.Surface
)

/**
 * Main theme composable for Maxmar Attendance app.
 * Supports both dark and light themes based on system preference or user choice.
 * 
 * @param darkTheme Whether to use dark theme. Defaults to system setting.
 * @param content The content to display with this theme.
 */
@Composable
fun MaxmarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val appColors = if (darkTheme) DarkAppColors else LightAppColors
    
    // Update status bar color based on theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MaxmarTypography,
            content = content
        )
    }
}

/**
 * Access app-specific colors from MaterialTheme.
 */
object MaxmarThemeColors {
    val appColors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}
