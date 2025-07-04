# Drone Mission Notes App

## Overview

Drone Mission Notes is an Android mobile application designed for drone operators to efficiently add, manage, and view notes related to their flight missions. The app provides a user-friendly interface to quickly jot down important details such as mission title, description, and location, with timestamps automatically recorded. Users can view all their notes, sort them by time or pinned status, edit existing notes, and delete them when no longer needed.

The application emphasizes local data storage for privacy and offline accessibility, utilizing a Room database. It features a modern UI built with Jetpack Compose and incorporates several bonus features like note searching, pinning, dynamic light/dark theme toggling, and GPS-based location auto-fill.

## Technologies and Tools Used

*   **Language:** Kotlin (100%)
*   **UI Framework:** Jetpack Compose (Material 3)
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **Asynchronous Programming:** Kotlin Coroutines & Flow
*   **Database:** Room Persistence Library (for local SQLite storage)
*   **Dependency Injection:** Hilt (Android)
*   **Navigation:** Navigation Compose
*   **Preferences:** Jetpack DataStore (for theme settings)
*   **Location Services:** Google Play Services Fused Location Provider API
*   **Build Tool:** Gradle
*   **Minimum Android Version:** Android 12 (API Level 31)

## Architecture and Folder Structure

The application follows the MVVM (Model-View-ViewModel) architectural pattern to promote a separation of concerns, testability, and maintainability.

Key package structure under `app/src/main/java/com/dunesaero/missionnotes/`:

*   **`data/`**: Contains data handling logic.
    *   **`local/`**: Room database components (DAO, Database class).
        *   `AppDatabase.kt`: Room database definition.
        *   `MissionNoteDao.kt`: Data Access Object for `MissionNote` entity.
    *   **`location/`**: Location fetching services.
        *   `LocationProvider.kt`: Manages GPS location fetching using Fused Location Provider.
    *   **`model/`**: Defines data entities.
        *   `MissionNote.kt`: Room entity representing a mission note.
    *   **`preferences/`**: Jetpack DataStore for user preferences.
        *   `ThemeSettingsManager.kt`: Manages theme (Light/Dark/System) preferences.
*   **`di/`**: Dependency Injection modules using Hilt.
    *   `AppModule.kt`: Provides singletons for Database, DAO, etc.
*   **`ui/`**: Jetpack Compose UI elements and ViewModels.
    *   **`screens/`**: Composable functions for different app screens.
        *   `AddNoteScreen.kt`: Screen for adding new notes.
        *   `EditNoteScreen.kt`: Screen for editing existing notes.
        *   `MainScreen.kt`: Hosts the NavHost and applies the theme.
        *   `NotesListScreen.kt`: Screen displaying the list of notes.
    *   **`theme/`**: Theming files for Jetpack Compose (Colors, Typography, Theme).
        *   `Color.kt`, `Theme.kt`, `Type.kt`.
    *   **`viewmodel/`**: ViewModels for each screen or shared logic.
        *   `NoteViewModel.kt`: Handles logic for note creation, retrieval, update, deletion, search, pinning, and location fetching.
        *   `SettingsViewModel.kt`: Manages theme settings.
*   **`MissionNotesApp.kt`**: The Application class, annotated with `@HiltAndroidApp`.
*   **`MainActivity.kt`**: The main entry point activity, hosts Jetpack Compose content.

## Setup and Build Instructions

1.  **Prerequisites:**
    *   Android Studio (latest stable version recommended, e.g., Hedgehog or newer).
    *   Android SDK Platform for API Level 31 (Android 12) or higher.
    *   Gradle configured for the project.

2.  **Clone the Repository:**
    ```bash
    git clone <repository_url>
    cd <project_directory>
    ```

3.  **Open in Android Studio:**
    *   Open Android Studio.
    *   Select "Open" or "Open an existing project".
    *   Navigate to the cloned project directory and select it.

4.  **Build the Project:**
    *   Android Studio should automatically sync Gradle files. If not, trigger a manual sync (File > Sync Project with Gradle Files or the elephant icon).
    *   Once synced, build the project (Build > Make Project or Ctrl+F9 / Cmd+F9).

5.  **Run the Application:**
    *   Select an emulator running Android 12 (API 31) or higher, or connect a physical device with Android 12+.
    *   Run the app (Run > Run 'app' or Shift+F10 / Ctrl+R).
    *   **Location Services:** For the GPS auto-fill feature to work, ensure location services are enabled on the test device/emulator, and grant location permission when prompted by the app.

## Known Issues or Limitations

*   **Snackbar Action Navigation:** The "Settings" action on Snackbars for location permission denial or GPS disabled navigates to the general app settings or system location settings. It does not directly toggle the permission/GPS state but provides a shortcut for the user to do so.
*   **Location Formatting:** GPS coordinates are displayed as raw latitude/longitude (formatted to 6 decimal places). Geocoding to a human-readable address is not implemented.
*   **Timestamp Editing:** The original timestamp of a note is preserved during editing and cannot be manually changed through the UI. This is by design to maintain the integrity of the note's creation time.
*   **Advanced Error Handling:** While basic error handling (e.g., for location fetching) is in place with Snackbars, more comprehensive error state management across the app could be implemented for production scenarios.

---
This README provides a good starting point. It can be further expanded with screenshots, detailed feature descriptions, or contribution guidelines if the project were to be developed further by a team.
