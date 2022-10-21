package com.fmspcoding.notesapp.presentation.note_draw_canvas

import com.fmspcoding.notesapp.domain.model.Note

data class NoteDrawState (
    val isLoading: Boolean = false,
    val note: Note? = null,
    val error: String = "",
)