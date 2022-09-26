package com.fmspcoding.notesapp.data.repository

import com.fmspcoding.notesapp.core.util.Resource
import com.fmspcoding.notesapp.data.local.NoteDao
import com.fmspcoding.notesapp.domain.model.Note
import com.fmspcoding.notesapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NoteRepositoryImpl(
    private val dao: NoteDao
): NoteRepository {
    override fun getNotes(): Flow<Resource<List<Note>>> = flow {
        emit(Resource.Loading())

        try {
            val notes = dao.getNotes().map { it.toNote() }
            emit(Resource.Success<List<Note>>(notes))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = "An error occurred."
            ))
        }
    }

    override fun getNote(noteId: Int): Flow<Resource<Note>> = flow {
        emit(Resource.Loading())

        try {
            val note = dao.getNote(noteId)
            emit(Resource.Success<Note>(note.toNote()))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = "An error occurred."
            ))
        }
    }

    override fun insertNote(note: Note): Flow<Resource<Note>> = flow {
        emit(Resource.Loading())

        try {
            dao.insertNote(note.toNoteEntity())
            emit(Resource.Success(note))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = "An error occurred."
            ))
        }
    }

    override fun deleteNote(noteId: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            dao.deleteNote(noteId)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = "An error occurred."
            ))
        }
    }

    override fun updateNote(note: Note): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            dao.updateNote(note.toNoteEntity())
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = "An error occurred."
            ))
        }
    }

}