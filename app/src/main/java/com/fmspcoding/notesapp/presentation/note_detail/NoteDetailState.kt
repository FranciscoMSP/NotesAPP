package com.fmspcoding.notesapp.presentation.note_detail

import com.fmspcoding.notesapp.domain.model.Note

data class NoteDetailState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val note: Note? = null,
    val error: String = ""
)