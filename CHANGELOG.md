# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Added
- **History Screen**: Implemented a new scrollable Month/Year picker in a bottom sheet to replace the arrow-based navigation.
  - Added `MonthYearScrollablePicker` composable using `LazyColumn` for efficient month selection.
  - Integrated `ModalBottomSheet` for a better user experience on mobile.
  - Supported a date range from 5 years past to 1 year future.
