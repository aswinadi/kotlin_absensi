package com.maxmar.attendance.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.maxmar.attendance.ui.screens.absent.AbsentScreen
import com.maxmar.attendance.ui.screens.auth.LoginScreen
import com.maxmar.attendance.ui.screens.checkin.CheckInScreen
import com.maxmar.attendance.ui.screens.checkin.CheckType
import com.maxmar.attendance.ui.screens.checkin.GeolocationMapScreen
import com.maxmar.attendance.ui.screens.history.HistoryScreen
import com.maxmar.attendance.ui.screens.home.HomeScreen
import com.maxmar.attendance.ui.screens.profile.ProfileScreen
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
            HomeScreen(
                onNavigateToHistory = {
                    navController.navigate(Routes.HISTORY)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onNavigateToNotifications = {
                    navController.navigate(Routes.NOTIFICATIONS)
                },
                onNavigateToAbsent = {
                    navController.navigate(Routes.ABSENT)
                },
                onNavigateToCheckIn = {
                    navController.navigate(Routes.CHECK_IN)
                },
                onNavigateToCheckOut = {
                    navController.navigate(Routes.CHECK_OUT)
                }
            )
        }
        
        // Check In Screen
        composable(
            route = Routes.CHECK_IN,
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
            CheckInScreen(
                checkType = CheckType.CHECK_IN,
                onNavigateBack = { navController.popBackStack() },
                onShowMap = { userLat, userLon, officeLat, officeLon, radius, officeName ->
                    navController.navigate(
                        Routes.geolocationMap(userLat, userLon, officeLat, officeLon, radius, officeName)
                    )
                }
            )
        }
        
        // Check Out Screen
        composable(
            route = Routes.CHECK_OUT,
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
            CheckInScreen(
                checkType = CheckType.CHECK_OUT,
                onNavigateBack = { navController.popBackStack() },
                onShowMap = { userLat, userLon, officeLat, officeLon, radius, officeName ->
                    navController.navigate(
                        Routes.geolocationMap(userLat, userLon, officeLat, officeLon, radius, officeName)
                    )
                }
            )
        }
        
        // Geolocation Map Screen
        composable(
            route = Routes.GEOLOCATION_MAP,
            arguments = listOf(
                navArgument("userLat") { type = NavType.StringType },
                navArgument("userLon") { type = NavType.StringType },
                navArgument("officeLat") { type = NavType.StringType },
                navArgument("officeLon") { type = NavType.StringType },
                navArgument("radius") { type = NavType.IntType },
                navArgument("officeName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            GeolocationMapScreen(
                userLat = backStackEntry.arguments?.getString("userLat")?.toDoubleOrNull() ?: 0.0,
                userLon = backStackEntry.arguments?.getString("userLon")?.toDoubleOrNull() ?: 0.0,
                officeLat = backStackEntry.arguments?.getString("officeLat")?.toDoubleOrNull() ?: 0.0,
                officeLon = backStackEntry.arguments?.getString("officeLon")?.toDoubleOrNull() ?: 0.0,
                radiusMeters = backStackEntry.arguments?.getInt("radius") ?: 100,
                officeName = backStackEntry.arguments?.getString("officeName")?.replace("_", "/") ?: "Kantor",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Face Capture Screen (legacy)
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
            // Redirect to CHECK_IN
            CheckInScreen(
                checkType = CheckType.CHECK_IN,
                onNavigateBack = { navController.popBackStack() },
                onShowMap = { userLat, userLon, officeLat, officeLon, radius, officeName ->
                    navController.navigate(
                        Routes.geolocationMap(userLat, userLon, officeLat, officeLon, radius, officeName)
                    )
                }
            )
        }
        
        // History Screen
        composable(Routes.HISTORY) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Profile Screen
        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        // Absent Screen
        composable(Routes.ABSENT) {
            AbsentScreen(
                onNavigateBack = { navController.popBackStack() }
            )
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
