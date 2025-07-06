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


