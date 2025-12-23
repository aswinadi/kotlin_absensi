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

## Data Model

### Approval.kt
```kotlin
val canEdit: Boolean
    get() {
        if (!isPendingAcknowledgement) return false
        val startDate = dateFormat.parse(date)
        return startDate.after(today)
    }
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

## Files

| File | Purpose |
|------|---------|
| `ApprovalScreen.kt` | UI with role-based buttons |
| `ApprovalViewModel.kt` | State management, API calls |
| `Approval.kt` | Data model with `canEdit` |
| `BusinessTrip.kt` | Data model with `canEdit` |
