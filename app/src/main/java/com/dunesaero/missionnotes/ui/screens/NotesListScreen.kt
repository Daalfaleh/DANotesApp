package com.dunesaero.missionnotes.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
// import androidx.compose.material.icons.Icons // Removed to avoid ambiguity
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness // For System
import androidx.compose.material.icons.filled.PushPin // Ensure this is present
import androidx.compose.material.icons.outlined.PushPin // Ensure this is present
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment // Added import
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dunesaero.missionnotes.data.model.MissionNote
import com.dunesaero.missionnotes.data.preferences.ThemeSetting
import com.dunesaero.missionnotes.ui.viewmodel.NoteViewModel
import com.dunesaero.missionnotes.ui.viewmodel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    noteViewModel: NoteViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(), // Add SettingsViewModel
    onAddNoteClicked: () -> Unit,
    onNoteClicked: (noteId: Int) -> Unit
) {
    val notes by noteViewModel.displayedNotes.collectAsState()
    val searchQuery by noteViewModel.searchQuery.collectAsState()
    val currentThemeSetting by settingsViewModel.themeSetting.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mission Notes") },
                actions = {
                    IconButton(onClick = {
                        val nextTheme = settingsViewModel.getNextThemeSetting()
                        settingsViewModel.setThemeSetting(nextTheme)
                    }) {
                        Icon(
                            imageVector = when (currentThemeSetting) {
                                ThemeSetting.LIGHT -> Icons.Filled.LightMode
                                ThemeSetting.DARK -> Icons.Filled.DarkMode
                                ThemeSetting.SYSTEM -> Icons.Filled.SettingsBrightness
                            },
                            contentDescription = "Toggle Theme"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNoteClicked) {
                Icon(Icons.Filled.Add, contentDescription = "Add Note")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { noteViewModel.onSearchQueryChanged(it) }, // Corrected to noteViewModel
                label = { Text("Search Notes (Title/Description)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { noteViewModel.onSearchQueryChanged("") }) { // Corrected to noteViewModel
                            Icon(Icons.Filled.Clear, contentDescription = "Clear Search")
                        }
                    }
                }
            )

            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f) // Takes remaining space
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (searchQuery.isNotEmpty()) "No notes found matching your search." else "No mission notes yet. Tap '+' to add one!")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f) // Takes remaining space
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notes, key = { note -> note.id }) { note ->
                        NoteItem(
                            note = note,
                            onClick = { onNoteClicked(note.id) },
                            onPinToggle = { noteViewModel.togglePinStatus(it) } // Corrected to noteViewModel
                        )
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteItem(
    note: MissionNote,
    onClick: () -> Unit,
    onPinToggle: (MissionNote) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp) // Adjust padding for IconButton
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f) // Main content takes available space
            ) {
                Text(text = note.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = note.description, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Location: ${note.location.ifEmpty { "N/A" }}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f, fill = false) // Prevent location text from pushing timestamp too far
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(note.timestamp)),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            IconButton(
                onClick = { onPinToggle(note) },
                modifier = Modifier.align(Alignment.Top) // Align pin to the top of the card's Row
            ) {
                Icon(
                    imageVector = if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                    contentDescription = if (note.isPinned) "Unpin Note" else "Pin Note",
                    tint = if (note.isPinned) MaterialTheme.colorScheme.primary else LocalContentColor.current.copy(alpha = LocalContentColor.current.alpha * 0.7f)
                )
            }
        }
    }
}
