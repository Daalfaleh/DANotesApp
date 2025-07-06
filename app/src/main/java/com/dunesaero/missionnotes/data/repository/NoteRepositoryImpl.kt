package com.dunesaero.missionnotes.data.repository

import com.dunesaero.missionnotes.data.local.MissionNoteDao
import com.dunesaero.missionnotes.data.model.MissionNote
import com.dunesaero.missionnotes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Implementation of NoteRepository that uses the DAO to access the Room database
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: MissionNoteDao
) : NoteRepository {

    override fun getAllNotes(): Flow<List<MissionNote>> {
        return noteDao.getAllNotes()
    }

    override suspend fun insert(note: MissionNote) {
        noteDao.insert(note)
    }

    override suspend fun update(note: MissionNote) {
        noteDao.update(note)
    }

    override suspend fun delete(note: MissionNote) {
        noteDao.delete(note)
    }

    override suspend fun deleteById(id: Int) {
        noteDao.deleteById(id)
    }

    override suspend fun getNoteById(id: Int): MissionNote? {
        return noteDao.getNoteById(id)
    }

    override fun searchNotes(query: String): Flow<List<MissionNote>> {
        return noteDao.searchNotes(query)
    }
}