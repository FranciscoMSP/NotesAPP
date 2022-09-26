package com.fmspcoding.notesapp.domain.model

import com.fmspcoding.notesapp.data.local.entity.NoteEntity

data class Note(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val checkItems: List<CheckItem> = emptyList()
) {
    fun toNoteEntity(): NoteEntity {
        return NoteEntity(
            id = id,
            title = title,
            description = description,
            checkItems = checkItems
        )
    }
}
