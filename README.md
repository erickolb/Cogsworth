# Cogsworth

A native Android browser and shuffle picker for a Discogs collection. Discogs custom collection folders are treated as physical record locations/shelves.

## Features

- Select a Discogs collection folder
- Browse by artist or album title
- Search artist, album, label, or genre
- Shuffle within the selected folder
- Move an album between existing Discogs collection folders
- Select and move multiple physical album instances with confirmation and progress
- Compact, non-scrolling album detail screen
- One-time personal-token setup, encrypted with Android Keystore
- Cogsworth launcher icon with in-app creator attribution

## Run

Open this directory in a current Android Studio installation, let Gradle sync, and run the `app` configuration on an Android 8.0+ device or emulator. The project targets Android API 37.

On first launch, enter the Discogs username and a personal access token created under **Discogs Settings → Developers**. The app requests collection data read-only. Custom folders are shown; Discogs' generated “All” folder is intentionally omitted.

## Notes

The app currently keeps fetched releases in memory for fast local sort/search/shuffle. A future offline-first release should add a Room cache and periodic WorkManager sync. For distribution to other account owners, replace personal-token setup with Discogs OAuth 1.0a.

## Icon attribution

<a href="https://www.flaticon.com/free-icons/ui" title="ui icons">Ui icons created by Rooman12 - Flaticon</a>
