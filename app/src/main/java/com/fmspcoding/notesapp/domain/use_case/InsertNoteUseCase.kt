package com.fmspcoding.notesapp.domain.use_case

import com.fmspcoding.notesapp.core.util.Resource
import com.fmspcoding.notesapp.domain.model.Note
import com.fmspcoding.notesapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InsertNoteUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(vararg note: Note): Flow<Resource<List<Long>>> {
//        if(note.title.isEmpty() && note.description.isEmpty() && note.checkItems.isEmpty()) {
//        }
        if(note.isEmpty()) {
            return flow { emit(Resource.Error(
                message = "É necessário preencher o titulo ou a descrição."
            )) }
        }

        return repository.insertNote(*note)
    }
}