package com.maxmar.attendance.data.api

import com.maxmar.attendance.BuildConfig

/**
 * API endpoint constants for the Maxmar Attendance API.
 */
object ApiEndpoints {
    val BASE_URL: String = BuildConfig.BASE_URL
    
    // Auth endpoints
    const val LOGIN = "login"
    const val LOGOUT = "logout"
    const val ME = "me"
    
    // Employee endpoints
    const val EMPLOYEE_PROFILE = "employee/profile"
    
    // Schedule endpoints
    const val MY_SCHEDULE = "schedule/my-schedule"
    const val TODAY_SHIFT = "schedule/today"
    
    // Attendance endpoints
    const val CHECK_IN = "attendance/check-in"
    const val CHECK_OUT = "attendance/check-out"
    const val ATTENDANCE_HISTORY = "attendance/history"
    
    // Absent endpoints
    const val ABSENT_TYPES = "absent-types"
    const val ABSENT_ATTENDANCE = "absent-attendance"
    
    // Approval endpoints
    const val APPROVALS = "approvals"
    fun approvalAcknowledge(id: Int) = "approvals/$id/acknowledge"
    fun approvalApprove(id: Int) = "approvals/$id/approve"
    fun approvalReject(id: Int) = "approvals/$id/reject"
    
    // Business Trip endpoints
    const val BUSINESS_TRIPS = "business-trips"
    fun businessTripDetail(id: Int) = "business-trips/$id"
    const val BUSINESS_TRIP_APPROVALS = "business-trips-approvals"
    fun businessTripAcknowledge(id: Int) = "business-trips/$id/acknowledge"
    fun businessTripApprove(id: Int) = "business-trips/$id/approve"
    fun businessTripReject(id: Int) = "business-trips/$id/reject"
    
    // Notification endpoints
    const val NOTIFICATIONS = "notifications"
    const val NOTIFICATIONS_UNREAD_COUNT = "notifications/unread-count"
    fun notificationMarkAsRead(id: String) = "notifications/$id/read"
    const val NOTIFICATIONS_MARK_ALL_READ = "notifications/read-all"
    
    // FCM Token endpoints
    const val FCM_TOKEN = "fcm-token"
}
