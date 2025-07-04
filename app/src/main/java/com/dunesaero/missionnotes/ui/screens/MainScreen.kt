package com.dunesaero.missionnotes.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dunesaero.missionnotes.ui.theme.DroneMissionNotesTheme
import com.dunesaero.missionnotes.ui.viewmodel.SettingsViewModel

object AppDestinations {
    const val NOTES_LIST_ROUTE = "notes_list"
    const val ADD_NOTE_ROUTE = "add_note"
    const val EDIT_NOTE_ROUTE_PATTERN = "edit_note/{noteId}"
    fun editNoteRoute(noteId: Int) = "edit_note/$noteId"
}

@Composable
fun MainScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val currentThemeSetting by settingsViewModel.themeSetting.collectAsState()

    DroneMissionNotesTheme(userThemeSetting = currentThemeSetting) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = AppDestinations.NOTES_LIST_ROUTE
        ) {
            composable(AppDestinations.NOTES_LIST_ROUTE) {
                NotesListScreen(
                    onAddNoteClicked = {
                        navController.navigate(AppDestinations.ADD_NOTE_ROUTE)
                    },
                    onNoteClicked = { noteId ->
                        navController.navigate(AppDestinations.editNoteRoute(noteId))
                    }
                )
            }
            composable(AppDestinations.ADD_NOTE_ROUTE) {
                AddNoteScreen(
                    onNoteAdded = {
                        navController.popBackStack() // Go back to list after adding
                    },
                    onNavigateBack = { navController.popBackStack() } // Added
                )
            }
            composable(AppDestinations.EDIT_NOTE_ROUTE_PATTERN) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
                if (noteId != null) {
                    EditNoteScreen(
                        noteId = noteId,
                        onNoteUpdated = { navController.popBackStack() },
                        onNoteDeleted = { navController.popBackStack() },
                        onNavigateBack = { navController.popBackStack() }
                    )
                } else {
                    // Handle error or navigate back if noteId is invalid
                    navController.popBackStack()
                }
            }
        }
    }
}
