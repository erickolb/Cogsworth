# Cogsworth

A native Android browser and shuffle picker for a Discogs collection. Discogs custom collection folders are treated as physical record locations/shelves.

[Download](https://github.com/erickolb/Cogsworth/releases/latest/download/Cogsworth.apk) · [Website](https://erickolb.github.io/Cogsworth/) · [Changelog](CHANGELOG.md) · [Release guide](RELEASING.md) · [Report a problem](https://github.com/erickolb/Cogsworth/issues)

## Features

- Select one or more Discogs collection folders
- Browse by artist or album title
- Search artist, album, label, or genre
- Shuffle within the selected folder
- Move an album between existing Discogs collection folders
- Select and move multiple physical album instances with confirmation and progress
- Kiosk-friendly 60-second idle mosaic with a continuously scrolling wall of album covers
- Compact, non-scrolling album detail screen with collection name
- Configurable personal Discogs ratings on details and listings, with tap-to-save whole-star editing
- One-time personal-token setup, encrypted with Android Keystore
- Cogsworth launcher icon with in-app creator attribution

## Run

Open this directory in a current Android Studio installation, let Gradle sync, and run the `app` configuration on an Android 8.0+ device or emulator. The project targets Android API 37.

On first launch, enter the Discogs username and a personal access token created under **Discogs Settings → Developers**. The app reads collection data and performs authenticated write operations when moving albums between folders or editing ratings. Custom folders are shown; Discogs' generated “All” folder is intentionally omitted.

## Ratings and Configuration

Open the **Configuration** gear between Reload and Credits on the shelf selection page to choose whether to display your personal Discogs ratings on Record Detail, allow editing there, and display ratings on Record Listing. All three switches default to On, and your preferences are saved on your device. Turning off detail ratings also turns off and disables editing; you can enable editing again after restoring detail ratings.

Ratings appear as five stars beneath the title divider on Record Detail and beneath the summarized metadata on Record Listing. Unrated records show empty stars, and five-star ratings glow more brightly. When editing is enabled, tap a detail star to save a rating directly to Discogs without a confirmation button. Discogs ratings use whole-star increments from 1 to 5; written reviews are not supported.

## Privacy

Cogsworth reads your collection and personal ratings directly from Discogs. When you move a record or tap a star to change its rating, the app sends the authenticated update directly to Discogs. Rating display and editing are optional; hiding ratings does not delete or change ratings already stored on Discogs or prevent collection data, including ratings, from being fetched. Configuration preferences are stored locally. Cogsworth has no developer-operated server, advertising, or analytics.

Read the full [app privacy policy](https://erickolb.github.io/Cogsworth/#privacy).

## Notes

The app currently keeps fetched releases in memory for fast local sort/search/shuffle. A future offline-first release should add a Room cache and periodic WorkManager sync. For distribution to other account owners, replace personal-token setup with Discogs OAuth 1.0a.

## Versioning

Cogsworth uses semantic versioning. The current public release is `1.1.0`. Patch releases increment automatically for compatible changes; major and minor version changes are chosen by the project owner.

## AI-generated code disclosure

This project contains code generated with assistance from OpenAI Codex. AI-generated contributions may contain errors or unexpected behavior and should be independently reviewed, tested, and validated before production use.

## Icon attribution

<a href="https://www.flaticon.com/free-icons/exhibition" title="exhibition icons">Exhibition icons created by Magnific - Flaticon</a>
