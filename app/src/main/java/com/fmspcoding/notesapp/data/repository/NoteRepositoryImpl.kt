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
    override fun getNotes(): Flow<List<Note>> {
        return dao.getNotes()
    }

    override fun getNote(noteId: Int): Flow<Resource<Note>> = flow {
        emit(Resource.Loading())

        try {
            val note = dao.getNote(noteId)
            emit(Resource.Success(note))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = "An error occurred."
            ))
        }
    }

    override fun insertNote(vararg note: Note): Flow<Resource<List<Long>>> = flow {
        emit(Resource.Loading())

        try {
            val listIds = dao.insertNote(*note)
            emit(Resource.Success(listIds))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = "An error occurred."
            ))
        }
    }

    override fun deleteNote(note: Note): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            dao.deleteNote(note)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = "An error occurred."
            ))
        }
    }

    override fun deleteNotes(idList: List<Int>): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            dao.deleteNotes(idList)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = "An error occurred."
            ))
        }
    }
}