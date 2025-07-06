package com.dunesaero.missionnotes.di

import android.app.Application
import androidx.room.Room
import com.dunesaero.missionnotes.data.local.AppDatabase
import com.dunesaero.missionnotes.data.local.MissionNoteDao
import com.dunesaero.missionnotes.data.repository.NoteRepositoryImpl
import com.dunesaero.missionnotes.domain.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "mission_notes_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(db: AppDatabase): MissionNoteDao {
        return db.noteDao()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(dao: MissionNoteDao): NoteRepository {
        return NoteRepositoryImpl(dao)
    }
}