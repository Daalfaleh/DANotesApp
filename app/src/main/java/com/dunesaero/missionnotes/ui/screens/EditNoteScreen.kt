package com.dunesaero.missionnotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable // Added import
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dunesaero.missionnotes.data.location.LocationResult
import com.dunesaero.missionnotes.data.model.MissionNote
import com.dunesaero.missionnotes.ui.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    noteId: Int,
    viewModel: NoteViewModel = hiltViewModel(),
    onNoteUpdated: () -> Unit,
    onNoteDeleted: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val noteState by viewModel.getNoteById(noteId).collectAsState()
    val locationResultViewModel by viewModel.locationResult.collectAsState() // Renamed to avoid clash

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") } // This will be updated by both noteState and locationResult
    var currentNote by remember { mutableStateOf<MissionNote?>(null) }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() } // For Snackbar

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineLocationGranted || coarseLocationGranted) {
            viewModel.fetchCurrentLocation()
        } else {
            // User denied permissions
            viewModel.clearLocationResult()
        }
    }

    // Effect to initialize fields from the loaded note
    LaunchedEffect(noteState) {
        noteState?.let {
            currentNote = it
            title = it.title
            description = it.description
            // Only set location from note if not already set by GPS attempt or if GPS attempt was null
            if (location.isEmpty() || locationResultViewModel == null || locationResultViewModel is LocationResult.Error || locationResultViewModel is LocationResult.PermissionDenied) {
                 if (it.location.isNotEmpty()) location = it.location
            }
        }
    }

    // Effect to update location field from GPS result
    LaunchedEffect(locationResultViewModel) {
        when (val result = locationResultViewModel) {
            is LocationResult.Success -> {
                location = String.format("%.6f, %.6f", result.latitude, result.longitude) // Format
                viewModel.clearLocationResult()
            }
            is LocationResult.Error -> {
                snackbarHostState.showSnackbar(
                    message = "Error fetching location: ${result.message}",
                    duration = SnackbarDuration.Short
                )
                viewModel.clearLocationResult()
            }
            is LocationResult.PermissionDenied -> {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = "Location permission denied. Please grant permission to use this feature.",
                    duration = SnackbarDuration.Long,
                    actionLabel = "Settings"
                )
                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = android.net.Uri.fromParts("package", context.packageName, null)
                    context.startActivity(intent)
                }
                viewModel.clearLocationResult()
            }
            is LocationResult.GpsDisabled -> {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = "GPS is disabled. Please enable GPS to fetch location.",
                    duration = SnackbarDuration.Long,
                    actionLabel = "Settings"
                )
                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    val intent = android.content.Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }
                viewModel.clearLocationResult()
            }
            else -> { /* Fetching or null state */ }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Add SnackbarHost
        topBar = {
            TopAppBar(
                title = { Text("Edit Mission Note") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete Note")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (currentNote == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // Show loading indicator
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }) {
                        if (locationResultViewModel is LocationResult.Fetching) { // Use locationResultViewModel here
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Filled.MyLocation, contentDescription = "Get Current Location")
                        }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                val isFormValid = title.isNotBlank() && description.isNotBlank()
                Button(
                    onClick = {
                        currentNote?.let {
                            if (isFormValid) { // Condition checked by enabled state
                                viewModel.updateNote(
                                    noteId = it.id,
                                    title = title.trim(),
                                    description = description.trim(),
                                    location = location.trim(),
                                    isPinned = it.isPinned,
                                    timestamp = it.timestamp
                                )
                                onNoteUpdated()
                            }
                        }
                    },
                    enabled = isFormValid, // Control button enabled state
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Note")
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this mission note? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteNote(noteId)
                        showDeleteConfirmDialog = false
                        onNoteDeleted()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
