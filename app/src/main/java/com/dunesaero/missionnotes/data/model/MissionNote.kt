package com.dunesaero.missionnotes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mission_notes")
data class MissionNote(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val location: String, // For now, user input. GPS can be added later.
    val timestamp: Long,
    val isPinned: Boolean = false // For bonus feature
)
