package com.dunesaero.missionnotes.di

import android.content.Context
import androidx.room.Room
import com.dunesaero.missionnotes.data.local.AppDatabase
import com.dunesaero.missionnotes.data.local.MissionNoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "mission_notes_db" // Consistent DB name
        ).fallbackToDestructiveMigration() // Added for schema changes during development
         .build()
    }

    @Provides
    @Singleton // DAOs are typically singletons or scoped
    fun provideMissionNoteDao(appDatabase: AppDatabase): MissionNoteDao {
        return appDatabase.missionNoteDao()
    }
}
