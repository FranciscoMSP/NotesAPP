package com.fmspcoding.notesapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val checkItems: List<CheckItem>,
    val drawName:String
) {
    fun toNoteItem(): NoteItem {
        return NoteItem(
            id = id,
            title = title,
            description = description,
            checkItems = checkItems,
            drawName = drawName
        )
    }
}
