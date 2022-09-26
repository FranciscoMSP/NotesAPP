package com.fmspcoding.notesapp.domain.use_case

import com.fmspcoding.notesapp.core.util.Resource
import com.fmspcoding.notesapp.domain.model.Note
import com.fmspcoding.notesapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetNotesUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<Resource<List<Note>>> {
        return repository.getNotes()
    }
}