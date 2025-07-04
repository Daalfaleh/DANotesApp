package com.dunesaero.missionnotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dunesaero.missionnotes.data.local.MissionNoteDao
import com.dunesaero.missionnotes.data.location.LocationProvider
import com.dunesaero.missionnotes.data.location.LocationResult
import com.dunesaero.missionnotes.data.model.MissionNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class) // For flatMapLatest
@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteDao: MissionNoteDao,
    private val locationProvider: LocationProvider // Inject LocationProvider
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val displayedNotes: StateFlow<List<MissionNote>> = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                noteDao.getAllNotes()
            } else {
                noteDao.searchNotes("%$query%")
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun getNoteById(noteId: Int): StateFlow<MissionNote?> {
        return noteDao.getNoteById(noteId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }

    fun addNote(title: String, description: String, location: String) {
        viewModelScope.launch {
            val newNote = MissionNote(
                title = title,
                description = description,
                location = location,
                timestamp = System.currentTimeMillis() // Automatically generate timestamp
            )
            noteDao.insertNote(newNote)
        }
    }

    fun updateNote(noteId: Int, title: String, description: String, location: String, isPinned: Boolean, timestamp: Long) {
        viewModelScope.launch {
            val updatedNote = MissionNote(
                id = noteId,
                title = title,
                description = description,
                location = location,
                isPinned = isPinned, // Keep original pinned status for now
                timestamp = timestamp // Keep original timestamp
            )
            noteDao.updateNote(updatedNote)
        }
    }

    fun deleteNote(noteId: Int) {
        viewModelScope.launch {
            noteDao.deleteNoteById(noteId)
        }
    }

    fun togglePinStatus(note: MissionNote) {
        viewModelScope.launch {
            val updatedNote = note.copy(isPinned = !note.isPinned)
            noteDao.updateNote(updatedNote)
        }
    }

    private val _locationResult = MutableStateFlow<LocationResult?>(null)
    val locationResult: StateFlow<LocationResult?> = _locationResult.asStateFlow()

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            locationProvider.getCurrentLocation().collect { result ->
                _locationResult.value = result
                // Optionally, reset after a short delay or when consumed if it's a one-time event
                if (result is LocationResult.Success || result is LocationResult.Error || result is LocationResult.PermissionDenied) {
                     // kotlinx.coroutines.delay(100) // short delay to allow UI to react
                     // _locationResult.value = null
                }
            }
        }
    }

    fun clearLocationResult() {
        _locationResult.value = null
    }
}
