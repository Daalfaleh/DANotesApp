package com.dunesaero.missionnotes.data.local

import androidx.room.*
import com.dunesaero.missionnotes.data.model.MissionNote
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionNoteDao {

    // Insert a note into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: MissionNote)

    // Update an existing note
    @Update
    suspend fun update(note: MissionNote)

    // Delete a note by reference
    @Delete
    suspend fun delete(note: MissionNote)

    // Delete a note by its ID
    @Query("DELETE FROM mission_notes WHERE id = :id")
    suspend fun deleteById(id: Int)

    // Get all notes ordered by pinned first and timestamp descending
    @Query("SELECT * FROM mission_notes ORDER BY isPinned DESC, timestamp DESC")
    fun getAllNotes(): Flow<List<MissionNote>>

    // Get a single note by its ID
    @Query("SELECT * FROM mission_notes WHERE id = :id")
    suspend fun getNoteById(id: Int): MissionNote?

    // Search notes by query matching title or description
    @Query("SELECT * FROM mission_notes WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Flow<List<MissionNote>>
}