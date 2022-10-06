package com.fmspcoding.notesapp.di

import android.app.Application
import androidx.room.Room
import com.fmspcoding.notesapp.data.local.Converters
import com.fmspcoding.notesapp.data.local.NoteDatabase
import com.fmspcoding.notesapp.data.repository.NoteRepositoryImpl
import com.fmspcoding.notesapp.data.util.GsonParser
import com.fmspcoding.notesapp.domain.repository.NoteRepository
import com.fmspcoding.notesapp.domain.use_case.*
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NoteModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).addTypeConverter(Converters(GsonParser(Gson())))
            .build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(
        db: NoteDatabase
    ): NoteRepository {
        return NoteRepositoryImpl(db.dao)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotesUseCase = GetNotesUseCase(repository),
            getNoteUseCase = GetNoteUseCase(repository),
            insertNoteUseCase = InsertNoteUseCase(repository),
            deleteNoteUseCase = DeleteNoteUseCase(repository),
            deleteNotesUseCase = DeleteNotesUseCase(repository)
        )
    }
}