# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

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
