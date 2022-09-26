package com.fmspcoding.notesapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fmspcoding.notesapp.domain.model.CheckItem
import com.fmspcoding.notesapp.domain.model.Note

@Entity
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val checkItems: List<CheckItem>
)
{
    fun toNote(): Note {
        return Note(
            id = id,
            title = title,
            description = description,
            checkItems = checkItems
        )
    }
}