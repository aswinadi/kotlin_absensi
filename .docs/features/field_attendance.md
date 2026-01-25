# Field Attendance Feature (Dinas Luar)

## Overview
Field Attendance allows employees to record their attendance when working remotely or visiting clients (Work From Anywhere / WFA). It involves a check-in process (Arrival) and a check-out process (Departure), both requiring photo evidence and GPS location.

## User Flow

### Arrival (Check-In)
1. Navigate to **Home** or **Attendance** screen.
2. Select **Dinas Luar** (Field Attendance).
3. Fill in the **Arrival Form**:
   - **Date**: Auto-filled with current date.
   - **Location Name**: Enter the name of the location (e.g., "PT Client ABC").
   - **Purpose**: Enter the purpose of the visit.
   - **Photo**: Take a photo of the location or selfie.
     - Supports **Front** and **Back** camera switching.
   - **Location**: Automatically captures GPS coordinates.
4. **Submit** the form.

### Departure (Check-Out)
1. Open an active Field Attendance record from:
   - **Home Screen**: Today's active attendance card.
   - **History Screen**: "Dinas Luar" tab, clicking on a record with "Sedang Berlangsung" status.
2. Review Arrival details.
3. Take a **Departure Photo**.
   - Supports **Front** and **Back** camera switching.
4. **Submit** to complete the attendance.

## Components

### FieldAttendanceFormScreen
Used for checking in (Arrival).

| Element | Description |
|---------|-------------|
| **Date Field** | Read-only, displays current date. |
| **Location Field** | Text input for location name. |
| **Purpose Field** | Multi-line text input for visit purpose. |
| **Camera View** | Full-screen camera with capture and switch camera buttons. |
| **GPS Indicator** | Shows current latitude/longitude. |

### FieldAttendanceDepartureScreen
Used for checking out (Departure).

| Element | Description |
|---------|-------------|
| **Arrival Card** | Summarizes arrival info (time, location, purpose, photo). |
| **Camera View** | Full-screen camera with capture and switch camera buttons. |
| **GPS Indicator** | Shows current latitude/longitude. |

## Key Features

### Camera enhancements
- **Front/Back Camera Toggle**: Users can switch between cameras to take selfies or environmental photos.
- **Image Rotation**: Automatically handles image rotation based on EXIF data.

### Location Validation
- **GPS Requirement**: Submission is disabled until a valid GPS location is acquired.
- **Mock Location Detection**: The system rejects fake GPS locations (Security Hardening).

## Data Model

### FieldAttendance
```kotlin
data class FieldAttendance(
    val id: Int,
    val date: String,
    val locationName: String,
    val purpose: String,
    val arrivalTime: String,
    val arrivalPhotoUrl: String?,
    val departureTime: String?,
    val departurePhotoUrl: String?,
    // ...
)
```

## Navigation Routes

| Route | Description |
|-------|-------------|
| `Routes.FIELD_ATTENDANCE_FORM` | Screen for Arrival Check-In. |
| `Routes.FIELD_ATTENDANCE_DEPARTURE` | Screen for Departure Check-Out (requires `id`). |
