package com.dunesaero.missionnotes.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dunesaero.missionnotes.data.model.MissionNote
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionNoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: MissionNote)

    @Update
    suspend fun updateNote(note: MissionNote)

    @Delete
    suspend fun deleteNote(note: MissionNote)

    @Query("SELECT * FROM mission_notes WHERE id = :id")
    fun getNoteById(id: Int): Flow<MissionNote?>

    @Query("SELECT * FROM mission_notes ORDER BY isPinned DESC, timestamp DESC")
    fun getAllNotes(): Flow<List<MissionNote>>

    @Query("DELETE FROM mission_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)

    // For search functionality (bonus)
    @Query("SELECT * FROM mission_notes WHERE title LIKE :searchQuery OR description LIKE :searchQuery ORDER BY isPinned DESC, timestamp DESC")
    fun searchNotes(searchQuery: String): Flow<List<MissionNote>>
}
