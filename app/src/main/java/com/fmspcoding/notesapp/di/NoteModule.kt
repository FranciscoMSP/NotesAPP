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
            app, NoteDatabase::class.java, "word_db"
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
    fun provideGetNotesUseCase(repository: NoteRepository): GetNotesUseCase {
        return GetNotesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetNoteUseCase(repository: NoteRepository): GetNoteUseCase {
        return GetNoteUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideInsertNoteUseCase(repository: NoteRepository): InsertNoteUseCase {
        return InsertNoteUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteNoteUseCase(repository: NoteRepository): DeleteNoteUseCase {
        return DeleteNoteUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateNoteUseCase(repository: NoteRepository): UpdateNoteUseCase {
        return UpdateNoteUseCase(repository)
    }

}