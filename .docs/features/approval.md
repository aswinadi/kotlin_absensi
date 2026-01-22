# Approval Screen

## Overview
The Approval screen displays pending and processed approval requests for leave (izin) and business trips (perdin). It implements role-based access control to differentiate between managers and staff.

## Role-Based Behavior

### Managers (Position Level ≤ 2)
- View all pending and processed approvals
- Can **Acknowledge** (Ketahui) requests pending acknowledgement
- Can **Approve** (Setujui) requests pending approval
- Can **Reject** (Tolak) any pending requests

### Staff (Position Level > 2)
- View only their own pending and processed approvals
- **View-only mode** for most items
- Can **Edit** their own pending requests if:
  - Status is `pending_acknowledgement` (no approval yet)
  - Start date is **in the future** (not today or past)

## Features

### Attachment Viewer
- Sick/leave requests with attachments display a **80dp thumbnail**
- Click "Lihat" button or thumbnail to open **full-screen viewer**
- Full-screen dialog with dark background and close button
- Supports viewing doctor's notes, medical certificates, etc.

### Type Badge
- Badge shows actual type name (e.g., "Sakit", "Cuti Tahunan", "Izin")
- Uses `approval.type?.name` instead of generic category

### Notification Deep Linking
- Tapping leave request push notification opens the app
- After splash/login, automatically navigates to Approval screen
- Supported notification types: `leave_request`, `approval`

## Data Model

### Approval.kt
```kotlin
val canEdit: Boolean
    get() {
        if (!isPendingAcknowledgement) return false
        val startDate = dateFormat.parse(date)
        return startDate.after(today)
    }

val attachment: String? = null  // URL to attachment image
```

### BusinessTrip.kt
```kotlin
val canEdit: Boolean
    get() {
        if (!acknowledgedBy.isNullOrEmpty()) return false
        val departureDate = dateFormat.parse(startDate)
        return departureDate.after(today)
    }
```

## Navigation Routes

| Route | Description |
|-------|-------------|
| `Routes.APPROVAL` | Approval list screen |
| `Routes.ABSENT_EDIT` | Edit absent attendance |
| `Routes.BUSINESS_TRIP_EDIT` | Edit business trip |

## Deep Link Data

| Field | Description |
|-------|-------------|
| `type` | `"leave_request"` or `"approval"` |
| `id` | Absent attendance ID |
| `action` | `"acknowledge"` or `"approve"` |

## Files

| File | Purpose |
|------|---------|
| `ApprovalScreen.kt` | UI with role-based buttons, attachment viewer |
| `ApprovalViewModel.kt` | State management, API calls |
| `Approval.kt` | Data model with `canEdit`, `attachment` |
| `BusinessTrip.kt` | Data model with `canEdit` |
| `Routes.kt` | Contains `DeepLinkData` class |
| `MaxmarNavHost.kt` | Handles deep link navigation |
| `MainActivity.kt` | Extracts deep link data from intent |

