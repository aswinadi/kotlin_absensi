package com.maxmar.attendance.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.maxmar.attendance.ui.screens.absent.AbsentDetailScreen
import com.maxmar.attendance.ui.screens.absent.AbsentScreen
import com.maxmar.attendance.ui.screens.approval.ApprovalScreen
import com.maxmar.attendance.ui.screens.auth.LoginScreen
import com.maxmar.attendance.ui.screens.businesstrip.BusinessTripDetailScreen
import com.maxmar.attendance.ui.screens.businesstrip.BusinessTripScreen
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
    navController: NavHostController = rememberNavController(),
    deepLinkData: DeepLinkData? = null,
    onDeepLinkHandled: () -> Unit = {}
) {
    // Handle deep link navigation after splash/login
    LaunchedEffect(deepLinkData) {
        if (deepLinkData != null) {
            // We'll handle this after the user is logged in (after splash screen)
            // The deep link will be processed when navigating to home
        }
    }

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
                    // Handle deep link after navigating to home
                    if (deepLinkData != null && (deepLinkData.type == "leave_request" || deepLinkData.type == "approval")) {
                        navController.navigate(Routes.APPROVAL)
                        onDeepLinkHandled()
                    }
                },
                onNavigateToProfileCompletion = {
                    navController.navigate(Routes.COMPLETE_PROFILE) {
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
                    // Handle deep link after login
                    if (deepLinkData != null && (deepLinkData.type == "leave_request" || deepLinkData.type == "approval")) {
                        navController.navigate(Routes.APPROVAL)
                        onDeepLinkHandled()
                    }
                },
                onRequiresProfileCompletion = {
                    navController.navigate(Routes.COMPLETE_PROFILE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Complete Profile Screen
        composable(Routes.COMPLETE_PROFILE) {
            com.maxmar.attendance.ui.screens.auth.CompleteProfileScreen(
                onProfileCompleted = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.COMPLETE_PROFILE) { inclusive = true }
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
                },
                onNavigateToBusinessTrip = {
                    navController.navigate(Routes.BUSINESS_TRIP)
                },
                onNavigateToApproval = {
                    navController.navigate(Routes.APPROVAL)
                },
                onNavigateToFieldAttendance = {
                    navController.navigate(Routes.FIELD_ATTENDANCE_FORM)
                },
                onNavigateToTeamFieldAttendance = {
                    navController.navigate(Routes.TEAM_FIELD_ATTENDANCE)
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
                },
                onNavigateToAbsentDetail = { id ->
                    navController.navigate(Routes.absentDetail(id))
                },
                onNavigateToFieldAttendanceDeparture = { id ->
                    navController.navigate(Routes.fieldAttendanceDeparture(id))
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
                },
                onNavigateToChangePassword = {
                    navController.navigate(Routes.CHANGE_PASSWORD)
                }
            )
        }
        
        // Change Password Screen
        composable(Routes.CHANGE_PASSWORD) {
            com.maxmar.attendance.ui.screens.profile.ChangePasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Absent Screen (Detail & Approval)
        composable(
            route = Routes.ABSENT_DETAIL,
            arguments = listOf(navArgument("absentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val absentId = backStackEntry.arguments?.getInt("absentId") ?: 0
            com.maxmar.attendance.ui.screens.absent.AbsentDetailScreen(
                absentId = absentId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Absent Screen (Create new)
        composable(Routes.ABSENT) {
            AbsentScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Absent Screen (Edit)
        composable(
            route = Routes.ABSENT_EDIT,
            arguments = listOf(navArgument("absentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val absentId = backStackEntry.arguments?.getInt("absentId") ?: 0
            // TODO: Pass absentId to AbsentScreen for editing
            // For now, navigate to regular AbsentScreen
            AbsentScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Business Trip Screen
        composable(Routes.BUSINESS_TRIP) {
            BusinessTripScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { tripId ->
                    navController.navigate(Routes.businessTripDetail(tripId.toString()))
                },
                onNavigateToCreate = {
                    navController.navigate(Routes.BUSINESS_TRIP_CREATE)
                },
                onNavigateToRealizationForm = { tripId ->
                    navController.navigate(Routes.realizationForm(tripId))
                }
            )
        }
        
        // Business Trip Create Screen
        composable(Routes.BUSINESS_TRIP_CREATE) {
            com.maxmar.attendance.ui.screens.businesstrip.BusinessTripFormScreen(
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
        
        // Business Trip Detail Screen
        composable(
            route = Routes.BUSINESS_TRIP_DETAIL,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")?.toIntOrNull() ?: 0
            BusinessTripDetailScreen(
                tripId = tripId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Routes.businessTripEdit(id))
                }
            )
        }
        
        // Business Trip Edit Screen
        composable(
            route = Routes.BUSINESS_TRIP_EDIT,
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0
            // TODO: Implement BusinessTripFormScreen with edit mode
            // For now, just navigate back to form screen
            com.maxmar.attendance.ui.screens.businesstrip.BusinessTripFormScreen(
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
        
        // Approval Screen
        composable(Routes.APPROVAL) {
            ApprovalScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { category, id ->
                    when (category) {
                        "izin" -> navController.navigate(Routes.absentEdit(id))
                        "perdin" -> navController.navigate(Routes.businessTripEdit(id))
                    }
                }
            )
        }
        
        // Notifications Screen
        composable(Routes.NOTIFICATIONS) {
            com.maxmar.attendance.ui.screens.notification.NotificationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Map Screen
        composable(Routes.MAP) {
            // TODO: MapScreen
        }
        
        // Realization List Screen
        composable(Routes.REALIZATION_LIST) {
            com.maxmar.attendance.ui.screens.realization.RealizationListScreen(
                onNavigateBack = { navController.popBackStack() },
                onTripSelected = { tripId ->
                    navController.navigate(Routes.realizationForm(tripId))
                }
            )
        }
        
        // Realization Form Screen
        composable(
            route = Routes.REALIZATION_FORM,
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0
            com.maxmar.attendance.ui.screens.realization.RealizationFormScreen(
                tripId = tripId,
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { 
                    navController.popBackStack(Routes.REALIZATION_LIST, inclusive = false)
                }
            )
        }

        // Field Attendance Form Screen (Dinas Luar - Create arrival)
        composable(
            route = Routes.FIELD_ATTENDANCE_FORM,
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
            com.maxmar.attendance.ui.screens.fieldattendance.FieldAttendanceFormScreen(
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        // Field Attendance Departure Screen
        composable(
            route = Routes.FIELD_ATTENDANCE_DEPARTURE,
            arguments = listOf(navArgument("fieldAttendanceId") { type = NavType.IntType })
        ) { backStackEntry ->
            val fieldAttendanceId = backStackEntry.arguments?.getInt("fieldAttendanceId") ?: 0
            com.maxmar.attendance.ui.screens.fieldattendance.FieldAttendanceDepartureScreen(
                fieldAttendanceId = fieldAttendanceId,
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        // Team Field Attendance Screen (Supervisor view)
        composable(Routes.TEAM_FIELD_ATTENDANCE) {
            com.maxmar.attendance.ui.screens.fieldattendance.TeamFieldAttendanceScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
