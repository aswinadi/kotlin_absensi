# Profile Screen

## Overview
Employee profile screen displaying personal and work information with logout capability.

## Status: ✅ Implemented (v0.2.0)

## Files
- `ui/screens/profile/ProfileScreen.kt` - Profile UI
- `ui/screens/profile/ProfileViewModel.kt` - State management

## Features
- Employee avatar placeholder
- Employee info display:
  - Full name
  - Employee code
  - Email
  - Phone
  - Department
  - Position
  - Company
  - Office location
- Logout button with navigation to Login
- Loading and error states
- Dark glassmorphism theme

## API Endpoint
- `GET /api/v1/employee/profile`

## Response Structure
```json
{
  "success": true,
  "data": {
    "employee": {
      "id": 1,
      "employee_code": "EMP-XXX-001",
      "full_name": "John Doe",
      "email": "john@example.com",
      "phone": "08123456789",
      "company": "PT Example",
      "department": "IT",
      "position": "Developer",
      "office": {
        "name": "Head Office",
        "latitude": -6.123,
        "longitude": 106.789,
        "radius": 100
      }
    },
    "schedule": {...},
    "leave_quota": {...}
  }
}
```

## Navigation
- Access: Bottom navigation → Profile
- Back: Returns to Home
- Logout: Navigates to Login (clears token)
