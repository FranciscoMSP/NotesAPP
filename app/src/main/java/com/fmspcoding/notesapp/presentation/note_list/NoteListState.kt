package com.fmspcoding.notesapp.presentation.note_list

import com.fmspcoding.notesapp.domain.model.Note
import com.fmspcoding.notesapp.domain.model.NoteItem

data class NoteListState(
    val isLoading:Boolean = false,
    val notes: List<Note> = emptyList(),
    val error: String  = ""
)
