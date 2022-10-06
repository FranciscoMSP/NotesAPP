package com.fmspcoding.notesapp.domain.use_case

import com.fmspcoding.notesapp.core.util.Resource
import com.fmspcoding.notesapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteNotesUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(idList: List<Int>): Flow<Resource<Unit>> {
        if(idList.isEmpty()) {
            return flow { }
        }
        return repository.deleteNotes(idList)
    }
}
