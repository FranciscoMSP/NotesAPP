package com.fmspcoding.notesapp.domain.use_case

import com.fmspcoding.notesapp.domain.model.Note
import com.fmspcoding.notesapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.getNotes()
    }
}