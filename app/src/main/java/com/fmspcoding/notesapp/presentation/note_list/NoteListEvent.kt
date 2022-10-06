package com.fmspcoding.notesapp.presentation.note_list

sealed class NoteListEvent {
    data class SelectItem(val currentValue: Boolean, val index: Int): NoteListEvent()
    object DeleteNotes: NoteListEvent()
    object CancelDelete: NoteListEvent()
    object RestoreNotes: NoteListEvent()
}