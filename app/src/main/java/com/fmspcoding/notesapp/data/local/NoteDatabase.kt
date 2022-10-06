package com.fmspcoding.notesapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fmspcoding.notesapp.domain.model.Note

@Database(
    entities = [Note::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class NoteDatabase: RoomDatabase() {

    abstract val dao: NoteDao

    companion object {
        const val DATABASE_NAME = "notes_db"
    }
}