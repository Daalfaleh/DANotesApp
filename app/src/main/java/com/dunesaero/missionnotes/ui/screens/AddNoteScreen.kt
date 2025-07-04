package com.dunesaero.missionnotes.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.dunesaero.missionnotes.ui.viewmodel.NoteViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    viewModel: NoteViewModel = hiltViewModel(),
    onNoteAdded: () -> Unit,
    onNavigateBack: () -> Unit // Added for back navigation from TopAppBar
) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val locationResult by viewModel.locationResult.collectAsState()
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
            viewModel.clearLocationResult() // Ensure no stale "Fetching" state
            // Optionally show a snackbar message that permission is needed
            // This case is also handled by LocationResult.PermissionDenied if LocationProvider sends it
        }
    }

    LaunchedEffect(locationResult) {
        when (val result = locationResult) {
            is LocationResult.Success -> {
                location = String.format("%.6f, %.6f", result.latitude, result.longitude) // Format to 6 decimal places
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
                    // Open app settings
                    val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = android.net.Uri.fromParts("package", context.packageName, null)
                    context.startActivity(intent)
                }
                viewModel.clearLocationResult()
            }
            is LocationResult.Fetching -> { /* Optionally show loading indicator */ }
            is LocationResult.GpsDisabled -> {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = "GPS is disabled. Please enable GPS to fetch location.",
                    duration = SnackbarDuration.Long,
                    actionLabel = "Settings"
                )
                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    // Open location settings
                    val intent = android.content.Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }
                viewModel.clearLocationResult()
            }
            null -> { /* Initial or cleared state */ }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Add SnackbarHost
        topBar = {
            TopAppBar(
                title = { Text("Add New Mission Note") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
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
                label = { Text("Location (Lat/Lon or Address)") },
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
                        if (locationResult is LocationResult.Fetching) {
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
                    // Condition is already checked by enabled state, but good for safety
                    if (isFormValid) {
                        viewModel.addNote(title.trim(), description.trim(), location.trim())
                        onNoteAdded()
                    }
                },
                enabled = isFormValid, // Control button enabled state
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Note")
            }
        }
    }
}
