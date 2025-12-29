# Realization Feature (Realisasi Perdin)

## Overview
The Realization feature allows employees to submit expense reports for approved business trips. After a business trip is completed, employees can record actual travel dates and all expenses incurred.

## User Flow
1. Navigate to **Perdin** tab from bottom navigation
2. Select **Realisasi** tab to see trips needing realization
3. Tap on a trip card to open the realization form
4. Fill in actual dates and expenses
5. Attach supporting documents (invoices/receipts)
6. Submit for processing

## Components

### RealizationListScreen
Lists business trips that need realization (approved but not yet realized).

| Element | Description |
|---------|-------------|
| Trip cards | Shows trip code, destination, purpose, dates |
| Status badge | Shows current realization status |
| FAB | Opens realization form for selected trip |

### RealizationFormScreen
Form for entering expense details.

#### Sections
1. **Trip Info Card** - Displays trip details (non-editable)
2. **Dates** - Actual departure/arrival dates
3. **Transport** - Plane, train, ship, bus/taxi expenses
4. **Accommodation** - Hotel, extra bed expenses
5. **Other Expenses** - Meals, toll, parking, other
6. **Documents** - Photo uploads for receipts/invoices
7. **Summary** - Total expenses vs. cash advance with difference calculation

#### Validation Rules
- Actual dates are **required**
- Each expense field accepts **numeric input only**
- Document uploads are **optional but recommended**
- Difference = Cash Advance - Total Expenses
  - Positive = Employee returns money
  - Negative = Company reimburses employee

## Data Model

### BusinessTripRealization
```kotlin
data class BusinessTripRealization(
    val id: Int,
    val businessTripId: Int,
    val actualDepartureDate: String?,
    val actualArrivalDate: String?,
    val transportPlane: Double,
    val transportTrain: Double,
    val transportShip: Double,
    val transportBusTaxi: Double,
    val accommodationHotel: Double,
    val accommodationExtraBed: Double,
    val meals: Double,
    val tollFee: Double,
    val parkingFee: Double,
    val otherExpense: Double,
    val notes: String?,
    val documents: List<RealizationDocument>
)
```

### Expense Categories
| Category | Fields |
|----------|--------|
| Transport | Plane, Train, Ship, Bus/Taxi |
| Accommodation | Hotel, Extra Bed |
| Others | Meals, Toll, Parking, Other |

## Navigation Routes

| Route | Description |
|-------|-------------|
| `Routes.REALIZATION_LIST` | List of trips needing realization |
| `Routes.REALIZATION_FORM` | Realization form (tripId required) |

## Files

| File | Purpose |
|------|---------|
| `RealizationListScreen.kt` | UI for trip list |
| `RealizationFormScreen.kt` | Form UI with expense inputs |
| `BusinessTripRealizationViewModel.kt` | State management |
| `BusinessTripRealization.kt` | Data model |
| `RealizationDocument.kt` | Document model |
