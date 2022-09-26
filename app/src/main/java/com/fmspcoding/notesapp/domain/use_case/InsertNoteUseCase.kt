package com.fmspcoding.notesapp.domain.use_case

import com.fmspcoding.notesapp.core.util.Resource
import com.fmspcoding.notesapp.domain.model.Note
import com.fmspcoding.notesapp.domain.repository.NoteRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InsertNoteUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(note: Note): Flow<Resource<Note>> {
        if(note.title.isEmpty() && note.description.isEmpty() && note.checkItems.isEmpty()) {
            return flow { }
        }
        return repository.insertNote(note)
    }
}