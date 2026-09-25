# Changelog

All notable changes to Cogsworth will be documented in this file.

Cogsworth uses semantic versioning. Unless the project owner designates a major or minor release, the next release increments the patch version.

## Unreleased

## 0.1.2 - 2026-09-25

### Changed

- Added a prominent Obtainium installation button to the website alongside the direct APK download.
- Inset the launcher artwork so it remains visible within rounded device icon masks and changed its background from beige to the pale blue used inside the display case.
- Kept the dark navigation-bar treatment compatible with Android 8.0 while applying the newer navigation-bar setting on Android 8.1 and later.

## 0.1.1 - 2026-09-25

### Changed

- Changed the permanent Android application ID, namespace, and Kotlin packages to `net.chateaulore.cogsworth` before the first public release.

### Added

- Added first-run Discogs setup instructions, an app privacy policy, Android version requirements, support links, and an independence notice to the project website.
- Added a documented versioning and changelog process.
- Added a tag-driven GitHub Actions workflow that tests, signs, verifies, and publishes release APKs with checksums.

## 0.1.0 - 2026-09-24

### Added

- Established the initial Cogsworth development baseline.
- Added browsing, searching, random selection, multi-folder selection, record movement, album details, and an idle album-cover mosaic.
- Added encrypted local storage for a Discogs username and personal access token.
