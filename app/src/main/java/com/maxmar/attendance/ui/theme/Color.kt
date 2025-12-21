package com.maxmar.attendance.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Maxmar Brand Colors
 * Based on the Flutter app theme - converted for Compose
 */
object MaxmarColors {
    // Primary Brand Colors
    val Primary = Color(0xFF8D0B12)        // Maxmar Dark Red
    val PrimaryDark = Color(0xFF6D0810)    // Darker Red
    val PrimaryLight = Color(0xFFB71C1C)   // Lighter Red
    
    // Semantic Colors
    val CheckIn = Color(0xFF2E7D32)        // Success Green
    val CheckOut = Color(0xFFC62828)       // Semantic Red
    val Absent = Color(0xFFEF6C00)         // Orange
    val Warning = Color(0xFFF57C00)        // Warning Orange
    val Error = Color(0xFFD32F2F)          // Error Red
    val Success = Color(0xFF4CAF50)        // Success Green
    
    // Gradient Colors (for buttons, headers)
    val GradientStart = Color(0xFFB71C1C)
    val GradientEnd = Color(0xFF8D0B12)
}

/**
 * Dark Theme Colors - Glassmorphism style
 */
object DarkColors {
    val Background = Color(0xFF0D0D0D)
    val BackgroundGradientStart = Color(0xFF0D0D0D)
    val BackgroundGradientEnd = Color(0xFF1A1A2E)
    
    val Surface = Color(0xFF1E1E1E)
    val SurfaceVariant = Color(0xFF2A2A2A)
    
    // Glassmorphism
    val GlassBackground = Color(0x14FFFFFF)  // 8% white
    val GlassBorder = Color(0x1FFFFFFF)      // 12% white
    val GlassHighlight = Color(0x0AFFFFFF)   // 4% white
    
    val OnBackground = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFFFFFFFF)
    val OnSurfaceVariant = Color(0xFFA0A0A0)
    
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFA0A0A0)
    val TextTertiary = Color(0xFF6B7280)
}

/**
 * Light Theme Colors - Clean modern style
 */
object LightColors {
    val Background = Color(0xFFF5F7FA)
    val BackgroundGradientStart = Color(0xFFF5F7FA)
    val BackgroundGradientEnd = Color(0xFFFFFFFF)
    
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF0F0F0)
    
    // Glassmorphism (subtle for light theme)
    val GlassBackground = Color(0xE6FFFFFF)  // 90% white
    val GlassBorder = Color(0x0F000000)      // 6% black
    val GlassHighlight = Color(0x0A000000)   // 4% black
    
    val OnBackground = Color(0xFF1A1A1A)
    val OnSurface = Color(0xFF1A1A1A)
    val OnSurfaceVariant = Color(0xFF6B7280)
    
    val TextPrimary = Color(0xFF1A1A1A)
    val TextSecondary = Color(0xFF6B7280)
    val TextTertiary = Color(0xFF9CA3AF)
}

/**
 * App-specific colors that adapt to theme.
 */
data class AppColors(
    val backgroundGradientStart: Color,
    val backgroundGradientEnd: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color
)

val DarkAppColors = AppColors(
    backgroundGradientStart = DarkColors.BackgroundGradientStart,
    backgroundGradientEnd = DarkColors.BackgroundGradientEnd,
    surface = DarkColors.Surface,
    surfaceVariant = DarkColors.SurfaceVariant,
    textPrimary = DarkColors.TextPrimary,
    textSecondary = DarkColors.TextSecondary,
    textTertiary = DarkColors.TextTertiary
)

val LightAppColors = AppColors(
    backgroundGradientStart = LightColors.BackgroundGradientStart,
    backgroundGradientEnd = LightColors.BackgroundGradientEnd,
    surface = LightColors.Surface,
    surfaceVariant = LightColors.SurfaceVariant,
    textPrimary = LightColors.TextPrimary,
    textSecondary = LightColors.TextSecondary,
    textTertiary = LightColors.TextTertiary
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }
