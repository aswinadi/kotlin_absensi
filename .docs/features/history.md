# History Screen

## Overview
Attendance history screen displaying past check-in/check-out records with pagination.

## Status: ✅ Implemented (v0.2.0)

## Files
- `ui/screens/history/HistoryScreen.kt` - History list UI
- `ui/screens/history/HistoryViewModel.kt` - State management with pagination
- `data/model/Attendance.kt` - Attendance data models
- `data/api/AttendanceApi.kt` - Retrofit interface
- `data/repository/AttendanceRepository.kt` - Data layer

## Features
- Lazy column with attendance cards
- Each card displays:
  - Date (formatted: "Senin, 21 Desember 2024")
  - Office name
  - Check-in time with radius status
  - Check-out time with radius status
- Pagination (loads more on scroll)
- Date filter (UI prepared, picker TODO)
- Loading, error, and empty states
- Pull-to-refresh ready

## API Endpoint
- `GET /api/v1/attendance/history`
- Query params: `start_date`, `end_date`, `page`

## Response Structure
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "date": "2024-12-21",
      "office": "Head Office",
      "check_in": {
        "time": "08:30:00",
        "latitude": -6.123,
        "longitude": 106.789,
        "is_within_radius": true
      },
      "check_out": {
        "time": "17:30:00",
        "latitude": -6.123,
        "longitude": 106.789,
        "is_within_radius": true
      }
    }
  ],
  "meta": {
    "current_page": 1,
    "last_page": 5,
    "per_page": 15,
    "total": 75
  }
}
```

## Navigation
- Access: Bottom navigation → History
- Back: Returns to Home
