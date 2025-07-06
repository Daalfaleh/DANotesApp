package com.dunesaero.missionnotes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dunesaero.missionnotes.data.model.MissionNote

@Database(entities = [MissionNote::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // Renamed to match what's used in AppModule.kt
    abstract fun noteDao(): MissionNoteDao
}