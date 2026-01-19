package com.maxmar.attendance.data.api

import com.maxmar.attendance.BuildConfig

/**
 * API endpoint constants for the Maxmar Attendance API.
 * 
 * Convention:
 * - /api/v1/auth/*  - Shared authentication endpoints
 * - /api/v1/att/*   - Attendance domain endpoints
 */
object ApiEndpoints {
    val BASE_URL: String = BuildConfig.BASE_URL
    
    // Auth endpoints (/api/v1/auth/*)
    const val LOGIN = "auth/login"
    const val LOGOUT = "auth/logout"
    const val ME = "auth/me"
    const val FCM_TOKEN = "auth/fcm-token"
    
    // Employee endpoints (/api/v1/att/*)
    const val EMPLOYEE_PROFILE = "att/employee/profile"
    
    // Schedule endpoints (/api/v1/att/*)
    const val MY_SCHEDULE = "att/schedules/my-schedule"
    const val TODAY_SHIFT = "att/schedules/today"
    
    // Attendance endpoints (/api/v1/att/*)
    const val CHECK_IN = "att/attendances/check-in"
    const val CHECK_OUT = "att/attendances/check-out"
    const val ATTENDANCE_HISTORY = "att/attendances/history"
    const val ATTENDANCE_SUMMARY = "att/attendances/summary"
    const val ATTENDANCE_MONTHLY_SUMMARY = "att/attendances/monthly-summary"
    
    // Absent endpoints (/api/v1/att/*)
    const val ABSENT_TYPES = "att/absent-types"
    const val ABSENT_ATTENDANCES = "att/absent-attendances"
    
    // Approval endpoints (/api/v1/att/*)
    const val APPROVALS = "att/approvals"
    fun approvalAcknowledge(id: Int) = "att/approvals/$id/acknowledge"
    fun approvalApprove(id: Int) = "att/approvals/$id/approve"
    fun approvalReject(id: Int) = "att/approvals/$id/reject"
    
    // Business Trip endpoints (/api/v1/att/*)
    const val BUSINESS_TRIPS = "att/business-trips"
    const val BUSINESS_TRIP_PURPOSES = "att/business-trip-purposes"
    const val BUSINESS_TRIP_DESTINATIONS = "att/business-trip-destinations"
    const val BUSINESS_TRIP_ASSIGNABLE_USERS = "att/business-trips/assignable-users"
    const val BUSINESS_TRIP_ALLOWANCE = "att/business-trips/allowance"
    fun businessTripDetail(id: Int) = "att/business-trips/$id"
    const val BUSINESS_TRIP_APPROVALS = "att/business-trips-approvals"
    fun businessTripAcknowledge(id: Int) = "att/business-trips/$id/acknowledge"
    fun businessTripApprove(id: Int) = "att/business-trips/$id/approve"
    fun businessTripReject(id: Int) = "att/business-trips/$id/reject"
    
    // Business Trip Realization endpoints (/api/v1/att/*)
    const val BUSINESS_TRIP_REALIZATION_APPROVALS = "att/business-trips-realization-approvals"
    fun businessTripRealization(tripId: Int) = "att/business-trips/$tripId/realization"
    fun businessTripRealizationDocument(tripId: Int, documentId: Int) = 
        "att/business-trips/$tripId/realization/documents/$documentId"
    fun realizationAcknowledge(id: Int) = "att/realizations/$id/acknowledge"
    fun realizationApprove(id: Int) = "att/realizations/$id/approve"
    fun realizationReject(id: Int) = "att/realizations/$id/reject"
    
    // Notification endpoints (/api/v1/att/*)
    const val NOTIFICATIONS = "att/notifications"
    const val NOTIFICATIONS_UNREAD_COUNT = "att/notifications/unread-count"
    fun notificationMarkAsRead(id: String) = "att/notifications/$id/read"
    const val NOTIFICATIONS_MARK_ALL_READ = "att/notifications/read-all"
    
    // Leave Quota endpoints (/api/v1/att/*)
    const val LEAVE_QUOTAS = "att/leave-quotas"
    const val LEAVE_QUOTA_TRANSACTIONS = "att/leave-quotas/transactions"
    
    // Holiday endpoints (/api/v1/att/*)
    const val HOLIDAYS = "att/holidays"
    const val HOLIDAYS_UPCOMING = "att/holidays/upcoming"
}
