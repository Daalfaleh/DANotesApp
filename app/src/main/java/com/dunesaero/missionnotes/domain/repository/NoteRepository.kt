package com.dunesaero.missionnotes.domain.repository

import com.dunesaero.missionnotes.data.model.MissionNote
import kotlinx.coroutines.flow.Flow

// Contract/interface for the repository, used by ViewModel and injected via Hilt
interface NoteRepository {

    // Fetch all notes from the database as a flow (real-time updates)
    fun getAllNotes(): Flow<List<MissionNote>>

    // Insert a new note
    suspend fun insert(note: MissionNote)

    // Update an existing note
    suspend fun update(note: MissionNote)

    // Delete a note
    suspend fun delete(note: MissionNote)

    // Delete a note by its ID
    suspend fun deleteById(id: Int)

    // Fetch a note by its ID
    suspend fun getNoteById(id: Int): MissionNote?

    // Search notes by query string
    fun searchNotes(query: String): Flow<List<MissionNote>>
}