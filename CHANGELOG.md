# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

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
