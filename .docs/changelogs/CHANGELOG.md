# Changelog

All notable changes to this project will be documented in this file.

## [0.5.0] - 2024-12-24

### Changed - Position Model Structure
- `Employee.kt` - Updated `position` from `String?` to `Position?` object
- Added `Position` data class with:
  - `id: Int?` - Position ID
  - `name: String` - Position name
  - `level: Int` - Hierarchy level (1=Director, 2=Manager, 3+=Staff)
  - `isManager: Boolean` - Computed property for role-based access

### Updated - ProfileScreen
- Updated display to use `position.name` instead of position string
- Compatible with both String and Object API responses via Gson deserialization

---

## [0.4.0] - 2024-12-24

### Added - Business Trip Realization
- `RealizationFormScreen.kt` - Form for entering expense realization
- `RealizationListScreen.kt` - List of trips needing realization
- `BusinessTripRealizationViewModel.kt` - State management for realization
- Document upload support for receipts/invoices
- Summary card with expense calculation (Cash Advance - Total = Difference)

### Added - Business Trip Tabs
- Perdin/Realisasi tab navigation in `BusinessTripScreen.kt`
- Filter chips for trip status (All, Pending, Approved, Completed)
- Realization status badge on trip cards

### Changed - Cash Advance
- Changed from automatic allowance calculation to manual cash advance input
- Cash advance field now editable in business trip form

### Fixed
- `purpose` property name alignment across all screens (was `purposeName`)
- Duplicate `category` property removed from `Approval.kt`
- Duplicate `CategoryTabsRow` removed from `ApprovalScreen.kt`

---

## [0.3.0] - 2024-12-23

### Added - Approval Role-Based Validation
- `canEdit` property to `Approval.kt` - Allows staff to edit own data if date is in future and not acknowledged
- `canEdit` property to `BusinessTrip.kt` - Same edit logic for business trips
- Role-based button visibility in `ApprovalScreen.kt`:
  - Managers (level ≤2): See Acknowledge/Approve/Reject buttons
  - Staff (level >2): See Edit button if editable, else view-only
- Edit button in `BusinessTripDetailScreen.kt` when trip is editable

### Added - Edit Routes
- `ABSENT_EDIT` route in `Routes.kt` for editing absent attendance
- `BUSINESS_TRIP_EDIT` route in `Routes.kt` for editing business trips
- Route handlers in `MaxmarNavHost.kt` for both edit screens

### Changed - Navigation
- Bottom navigation bar now has 5 items: Home, Perdin, History, Approval, Profile
- Moved "Perdin" (Business Trip) from action buttons to bottom nav
- Simplified home action buttons to: Check In, Check Out, Pengajuan Izin/Cuti
- Business Trip list screen accessible via bottom nav with FAB for creating new trips

---

## [0.2.0] - 2024-12-21

### Added - Profile Screen
- `ProfileScreen.kt` - Employee profile UI with dark glassmorphism theme
- `ProfileViewModel.kt` - State management with logout functionality
- Settings card with dark mode toggle switch
- Displays employee code, name, email, phone, department, position, company, office

### Added - History Screen
- `HistoryScreen.kt` - Attendance history with lazy list and pagination
- `HistoryViewModel.kt` - State management with date filtering support
- `Attendance.kt` - Model for attendance records with check-in/check-out data
- `AttendanceApi.kt` - Retrofit interface for attendance endpoints
- `AttendanceRepository.kt` - Data layer for fetching attendance history

### Added - Theme Toggle
- `SettingsManager.kt` - DataStore persistence for dark mode preference
- Dark/Light mode switching with toggle in Profile screen
- Theme applied globally via MainActivity

### Fixed
- Employee profile API response parsing (nested `data.employee` structure)
- Updated `Employee.kt` model to match Laravel API response
- Added `EmployeeProfileData`, `ScheduleInfo`, `LeaveQuota` models
- Fixed `company` field mapping (was `companyName`)

### Changed
- `EmployeeApi.kt` - Returns `EmployeeProfileData` for profile endpoint
- `EmployeeRepository.kt` - Extracts employee from nested response
- `NetworkModule.kt` - Added `AttendanceApi` provider
- `MaxmarNavHost.kt` - Wired up Profile and History screen navigation

---

## [0.1.0] - 2024-12-20

### Added - Project Foundation
- Initial project setup with Jetpack Compose
- Hilt dependency injection configuration
- Retrofit API client with AuthInterceptor
- DataStore for token management
- Dark theme with Maxmar branding colors

### Added - Authentication
- `LoginScreen.kt` - Login UI with email/password
- `AuthViewModel.kt` - Authentication state management
- `AuthRepository.kt` - Login/logout API operations
- `TokenManager.kt` - Secure token storage with DataStore

### Added - Home Screen
- `HomeScreen.kt` - Dashboard with schedule, action buttons, stats
- `HomeViewModel.kt` - Employee and shift data loading
- Bottom navigation bar (Home, History, Profile)

### Added - Navigation
- `MaxmarNavHost.kt` - Navigation graph with all routes
- `Routes.kt` - Route constants for all screens
- Splash → Login → Home flow

### Added - API Integration
- ngrok support with `ngrok-skip-browser-warning` header
- `ApiEndpoints.kt` - All API endpoint constants
- Connection to Laravel `filament_absensi` backend
