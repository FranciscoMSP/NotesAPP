package com.fmspcoding.notesapp.domain.model

data class NoteItem(
    val id: Int = 0,
    val title: String,
    val description: String,
    val checkItems: List<CheckItem>,
    val selected: Boolean = false
)
