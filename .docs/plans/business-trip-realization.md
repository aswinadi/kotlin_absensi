# Business Trip Realization - Implementation Plan

## Goal

Enable employees to submit realization reports for completed business trips, including expense documentation with multiple document uploads, and implement the approval workflow.

---

## Current State

### Backend (filament_absensi) ✅ Already Implemented
- `BusinessTripRealization` model with Spatie Media Library
- `BusinessTripRealizationController` with CRUD + document upload
- Approval endpoints (acknowledge/approve/reject)
- API routes already defined

### Android App (kotlin_absensi) ❌ Needs Implementation
- No realization screens exist
- No realization data model
- No document upload for expenses

---

## Features Required

1. **Multiple Document Upload** - Upload invoices/receipts from camera, gallery, or files for each expense category
2. **Expense Summary** - Show total expense vs cash advance received with difference calculation
3. **Approval Workflow** - Same acknowledge → approve flow as business trips
4. **Pending Realization Alert** - Show trips that have ended but don't have realization data yet

---

## Allowance / Cash Advance Flow

### Business Trip Creation
- **Allowance is OPTIONAL** - User manually inputs cash advance amount (if any)
- NOT auto-calculated from position × days
- Field: `cash_advance` (nullable, default 0)

### Realization
- **Cash Advance Reference** - Pre-filled from business trip's `cash_advance`
- **Calculation**: `Difference = Cash Advance - Total Expense`
  - Positive difference = Employee returns money
  - Negative difference = Company reimburses employee

```
Business Trip (creation)          Realization (after trip)
┌─────────────────────┐           ┌─────────────────────┐
│ Cash Advance: Rp 2jt│  ──────>  │ Cash Advance: Rp 2jt│ (read-only)
│ (optional input)    │           │ Total Expense: Rp 1.8jt│
└─────────────────────┘           │ Difference: Rp 200rb│ (return)
                                  └─────────────────────┘
```

---

## Implementation Phases

### Phase 1: Data Models (kotlin_absensi)
- `BusinessTripRealization.kt` - Main realization model
- `TransportExpense.kt`, `AccommodationExpense.kt` - Nested expense models
- `RealizationDocument.kt` - Document model

### Phase 2: API Layer (kotlin_absensi)
- Add realization endpoints to `BusinessTripApi.kt`
- Multipart upload for documents

### Phase 3: Repository Layer (kotlin_absensi)
- Add realization methods to `BusinessTripRepository.kt`
- Handle file uploads with `MultipartBody`

### Phase 4: UI Screens (kotlin_absensi)
- `BusinessTripRealizationListScreen.kt` - List trips needing realization
- `BusinessTripRealizationFormScreen.kt` - Create/edit form with document upload
- `BusinessTripRealizationDetailScreen.kt` - View submitted realization

### Phase 5: ViewModel (kotlin_absensi)
- `BusinessTripRealizationViewModel.kt` - State management + expense calculations

### Phase 6: Navigation (kotlin_absensi)
- Add routes to `Routes.kt` and `MaxmarNavHost.kt`
- Add "Realisasi" tab to `BusinessTripScreen.kt`

### Phase 7: Backend Enhancement (filament_absensi)
- Add `tripsNeedingRealization()` endpoint
- Update routes

---

## UI Flow

```
Bottom Nav: "Perdin"
    │
    └── Business Trip Screen
            │
            ├── Tab: "Perjalanan" (existing trips)
            │
            └── Tab: "Realisasi" (new)
                    │
                    ├── Trips needing realization (yellow badge)
                    │       └── Tap → Realization Form
                    │
                    └── Submitted realizations
                            └── Tap → Realization Detail
```

---

## Expense Summary Design

```
┌─────────────────────────────────────────┐
│ RINGKASAN BIAYA                         │
├─────────────────────────────────────────┤
│ Transport                     Rp 500.000│
│ Akomodasi                     Rp 800.000│
│ Makan                         Rp 200.000│
│ Tol                            Rp 50.000│
│ Parkir                         Rp 30.000│
│ Lainnya                       Rp 100.000│
├─────────────────────────────────────────┤
│ TOTAL PENGELUARAN           Rp 1.680.000│
│ Uang Muka Diterima          Rp 2.000.000│
├─────────────────────────────────────────┤
│ SELISIH (Kembalikan)          Rp 320.000│
└─────────────────────────────────────────┘
```

### Expense Categories

| Category | Description | Invoice Toggle |
|----------|-------------|----------------|
| **Transport** | Plane, Train, Ship, Bus/Taxi | Yes (each) |
| **Akomodasi** | Hotel, Extra Bed | Yes (each) |
| **Makan** | Meal expenses | Yes |
| **Tol** | Toll fees | Yes |
| **Parkir** | Parking fees | Yes |
| **Lainnya** | Other expenses not in categories above | Yes |

> **Note:** Cash Advance (Uang Muka) is taken from the optional amount entered when creating the business trip.

---

## Files to Create

### kotlin_absensi (New)
| File | Description |
|------|-------------|
| `BusinessTripRealization.kt` | Data model |
| `BusinessTripRealizationListScreen.kt` | List screen |
| `BusinessTripRealizationFormScreen.kt` | Form with document upload |
| `BusinessTripRealizationDetailScreen.kt` | Detail view |
| `BusinessTripRealizationViewModel.kt` | ViewModel |

### kotlin_absensi (Modify)
| File | Change |
|------|--------|
| `BusinessTripApi.kt` | Add realization endpoints |
| `BusinessTripRepository.kt` | Add realization methods |
| `Routes.kt` | Add realization routes |
| `MaxmarNavHost.kt` | Add navigation |
| `BusinessTripScreen.kt` | Add "Realisasi" tab |

### filament_absensi (Modify)
| File | Change |
|------|--------|
| `BusinessTripController.php` | Add `tripsNeedingRealization()` |
| `routes/api.php` | Add new endpoint |

---

## Priority Order

1. Backend API endpoint for trips needing realization
2. Data models for realization
3. API layer + Repository
4. Realization list screen (with tabs)
5. Realization form screen (with document upload)
6. Realization detail screen
7. Navigation wiring
8. Testing

---

## Related Files
- Model: `app/Models/BusinessTripRealization.php`
- Controller: `app/Http/Controllers/Api/V1/BusinessTripRealizationController.php`
- Routes: `routes/api.php`
