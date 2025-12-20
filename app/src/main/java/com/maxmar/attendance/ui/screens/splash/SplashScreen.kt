package com.maxmar.attendance.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxmar.attendance.R
import com.maxmar.attendance.ui.theme.DarkColors
import com.maxmar.attendance.ui.theme.MaxmarColors
import kotlinx.coroutines.delay

/**
 * Splash screen with animated logo and loading indicator.
 * Navigates to login after a brief delay.
 */
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // Animation values
    val fadeAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.8f) }
    
    // Glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    
    // Start animations
    LaunchedEffect(Unit) {
        fadeAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
    }
    
    // Navigate to login after delay (simplified - skip auth check for now)
    LaunchedEffect(Unit) {
        delay(2500)
        onNavigateToLogin()
    }
    
    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkColors.BackgroundGradientStart,
                        DarkColors.BackgroundGradientEnd
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(fadeAnim.value)
                .scale(scaleAnim.value)
        ) {
            // Logo image with glow effect
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = (20 * glowAlpha.value).dp,
                        spotColor = MaxmarColors.Primary,
                        ambientColor = MaxmarColors.Primary,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.maxmar_logo),
                    contentDescription = "Maxmar Logo",
                    modifier = Modifier.size(200.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "ATTENDANCE",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 8.sp
            )
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Loading indicator
            LinearProgressIndicator(
                modifier = Modifier
                    .width(120.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp)),
                color = MaxmarColors.Primary,
                trackColor = DarkColors.GlassBackground
            )
        }
        
        // Version text at bottom
        Text(
            text = "v1.0.0",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
