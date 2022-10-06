package com.fmspcoding.notesapp.domain.repository

import com.fmspcoding.notesapp.core.util.Resource
import com.fmspcoding.notesapp.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(): Flow<List<Note>>
    fun getNote(noteId: Int): Flow<Resource<Note>>
    fun insertNote(vararg note: Note): Flow<Resource<Unit>>
    fun deleteNote(note: Note): Flow<Resource<Unit>>
    fun deleteNotes(idList: List<Int>): Flow<Resource<Unit>>
}