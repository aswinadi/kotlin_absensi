# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

## [1.5.1] - 2026-01-24

### Added
- **Changelog Consolidation**: Merged root `CHANGELOG.md` and historical logs into `.docs/changelogs/CHANGELOG.md`.

### Fixed
- **Navigation**: Enabled navigation to **Field Attendance Departure** screen by clicking on active (In Progress) cards in the History screen.

## [1.5.0] - 2026-01-23

### Added
- **Supervisor Dashboard**:
  - **Supervisor Section**: Added new section on Home Screen for Supervisors (Position Level < 5).
  - **Team Field Attendance**: View list of team members' field attendance (Today & Upcoming).
  - **Navigation**: Moved "Approval" menu to Supervisor section.
- **Sequential Approval**: Enforced a strict "Acknowledge before Approve" workflow. Actions for higher-level supervisors are hidden until Level-1 acknowledgement is complete.
- **Auto-Login**: Added "Remember Me" functionality to Login screen with persistent session handling in Splash screen.

### Changed
- **Home Screen**: Updated layout to accommodate Supervisor section.
- **Field Attendance**: Added integration for fetching team field attendance status.
- **Refined Permissions**: Supervisor action buttons are now shown dynamically based on relative position level (requester level vs current user level).

### Fixed
- **Stability**: Resolved 80+ compilation errors caused by missing Compose imports and property name mismatches (`fullName`, `employeeCode`, `date`).
- **Navigation**: Cleaned up navigation callbacks in `HistoryScreen` and `MaxmarNavHost`.

## [1.4.0] - 2026-01-22

### Added
- **Approval Screen**:
  - **Attachment Viewer**: Added full-screen dialog to view attachment images clearly with zoom support.
  - **Notification Deep Linking**: Tapping leave request notifications now navigates directly to the Approval screen.
  
### Fixed
- **Approval Screen**:
  - **Type Badge**: Badge now shows actual type name (e.g., "Sakit", "Cuti Tahunan") instead of generic "Izin".
  - **Attachment Thumbnail**: Reduced thumbnail size from 180dp to 80dp with "Lihat" button for better card layout.

## [1.3.0] - 2026-01-22

### Added
- **Field Attendance**:
  - **Front Camera Support**: Users can now use the front camera for taking photos during Check-In and Check-Out in Field Attendance (WFA) mode.
  - **Camera Toggle**: Added a button to switch between front and back cameras.

### Fixed
- **Build & Theme**:
  - **Dependency Injection**: Resolved Hilt/KSP dependency issues preventing build.
  - **Theming**: Migrated hardcoded colors to `MaterialTheme` and `LocalAppColors` in Field Attendance screens (Departure & Form).
  - **WFA Check**: Fixed validation logic for Work From Anywhere (WFA) check-in/out bypass.

## [1.2.0] - 2026-01-19

### Added
- **Security Hardening**:
  - **Fake GPS Detection**: Implemented strict validation to reject locations from mock providers.
  - **Trusted Time**: Switched attendance timestamps to use high-accuracy GPS Time (`location.time`) instead of device system time to prevent date/time manipulation.

## [1.1.0] - 2026-01-19

### Added
- **Permissions**: Request Camera and Location permissions immediately on Home Screen entry.
- **History Screen**: Implemented a new scrollable Month/Year picker in a bottom sheet.
- **Privacy**: Face data is now cropped locally from downloaded reference photos to ensure privacy and matching accuracy.

### Fixed
- **Face Recognition**:
  - Implemented **Local Face Cropping** for downloaded photos using ML Kit. This fixes 0% match issues with uncropped profile photos.
  - Resolved "0% Match" issue by ensuring correct photo URL accessibility via `ngrok` for development builds.
- **Firebase Notifications**:
  - Fixed payload key mismatch for device token registration (`token` instead of `fcm_token`).
  - Added repository logic to sync token on login.
- **Timezone Display**:
  - Updated Backend API to return ISO 8601 timestamps (UTC).
  - Updated Android App to parse ISO dates and display them in Local Timezone.
- **UI Improvements**:
  - **Today Card**: Simplified check-in/out display to HH:mm (removed date).
  - Changed date/time format on Home/History to readable `dd-MM-yyyy HH:mm`.
  - Added "Dev" and "Debug" indicators in app footer.

---

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
