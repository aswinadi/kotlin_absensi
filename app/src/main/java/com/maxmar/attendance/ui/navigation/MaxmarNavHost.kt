package com.maxmar.attendance.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maxmar.attendance.ui.screens.auth.LoginScreen
import com.maxmar.attendance.ui.screens.splash.SplashScreen

/**
 * Main navigation host for the app.
 * Defines all navigation routes and their corresponding screens.
 */
@Composable
fun MaxmarNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300))
        }
    ) {
        // Splash Screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        
        // Login Screen
        composable(
            route = Routes.LOGIN,
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        
        // Home Screen
        composable(
            route = Routes.HOME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            }
        ) {
            // TODO: HomeScreen
        }
        
        // Face Capture Screen
        composable(
            route = Routes.FACE_CAPTURE,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(300)
                )
            }
        ) {
            // TODO: FaceCaptureScreen
        }
        
        // History Screen
        composable(Routes.HISTORY) {
            // TODO: HistoryScreen
        }
        
        // Profile Screen
        composable(Routes.PROFILE) {
            // TODO: ProfileScreen
        }
        
        // Absent Screen
        composable(Routes.ABSENT) {
            // TODO: AbsentScreen
        }
        
        // Business Trip Screen
        composable(Routes.BUSINESS_TRIP) {
            // TODO: BusinessTripScreen
        }
        
        // Approval Screen
        composable(Routes.APPROVAL) {
            // TODO: ApprovalScreen
        }
        
        // Notifications Screen
        composable(Routes.NOTIFICATIONS) {
            // TODO: NotificationScreen
        }
        
        // Map Screen
        composable(Routes.MAP) {
            // TODO: MapScreen
        }
    }
}
