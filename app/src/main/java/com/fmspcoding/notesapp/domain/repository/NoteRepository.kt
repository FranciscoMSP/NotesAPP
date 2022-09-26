package com.fmspcoding.notesapp.domain.repository

import com.fmspcoding.notesapp.core.util.Resource
import com.fmspcoding.notesapp.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(): Flow<Resource<List<Note>>>
    fun getNote(noteId: Int): Flow<Resource<Note>>
    fun insertNote(note: Note): Flow<Resource<Note>>
    fun deleteNote(noteId: Int): Flow<Resource<Unit>>
    fun updateNote(note: Note): Flow<Resource<Unit>>
}