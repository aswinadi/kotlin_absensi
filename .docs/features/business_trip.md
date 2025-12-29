# Business Trip Feature (Perdin)

## Overview
The Business Trip feature allows employees to create, manage, and track business trip requests and realizations. It is accessible via the bottom navigation bar.

## Screen Structure
The main `BusinessTripScreen` uses a **tabbed interface**:

| Tab | Content |
|-----|---------|
| **Perdin** | List of business trip requests |
| **Realisasi** | Trips needing expense realization |

## Perdin Tab

### Features
- List all business trips for the logged-in employee
- Filter by status (All, Pending, Approved, Completed)
- Each card shows:
  - Trip code
  - Destination
  - Purpose
  - Travel dates
  - Status badge
- FAB to create new trip request
- Tap card to view trip details

### Filter Options
| Filter | Description |
|--------|-------------|
| Semua | All trips |
| Menunggu | Pending approval |
| Disetujui | Approved trips |
| Selesai | Completed trips |

## Realisasi Tab

### Features
- Shows approved trips that need expense realization
- Displays cash advance and allowance amounts
- Progress indicator for partially completed realizations
- Tap to open realization form

See [realization.md](./realization.md) for details.

## Data Model

### BusinessTrip
```kotlin
data class BusinessTrip(
    val id: Int,
    val code: String,
    val employeeId: Int,
    val purpose: String,        // Trip purpose/reason
    val destination: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val totalAllowance: Double,
    val cashAdvance: Double,
    val acknowledgedBy: String?,
    val approvedBy: String?,
    val canEdit: Boolean        // Computed property
)
```

### Status Values
| Status | Description |
|--------|-------------|
| `pending_acknowledgement` | Waiting for manager acknowledgement |
| `pending_approval` | Acknowledged, waiting for approval |
| `approved` | Fully approved for travel |
| `rejected` | Request denied |
| `completed` | Trip completed with realization |

### Edit Rules (canEdit property)
A business trip can be edited if:
1. It has **not been acknowledged** yet (`acknowledgedBy` is null)
2. Departure date is **in the future**

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
| `Routes.BUSINESS_TRIP` | Main screen with tabs |
| `Routes.BUSINESS_TRIP_DETAIL` | Trip details |
| `Routes.BUSINESS_TRIP_CREATE` | Create new trip |
| `Routes.BUSINESS_TRIP_EDIT` | Edit existing trip |

## Files

| File | Purpose |
|------|---------|
| `BusinessTripScreen.kt` | Main UI with Perdin/Realisasi tabs |
| `BusinessTripDetailScreen.kt` | Trip details view |
| `BusinessTripFormScreen.kt` | Create/edit form |
| `BusinessTripViewModel.kt` | State management |
| `BusinessTrip.kt` | Data model |
