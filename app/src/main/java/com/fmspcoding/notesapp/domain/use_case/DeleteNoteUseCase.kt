package com.fmspcoding.notesapp.domain.use_case

import com.fmspcoding.notesapp.core.util.Resource
import com.fmspcoding.notesapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteNoteUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(noteId: Int): Flow<Resource<Unit>> {
        if(noteId <= 0) {
            return flow { }
        }
        return repository.deleteNote(noteId)
    }
}