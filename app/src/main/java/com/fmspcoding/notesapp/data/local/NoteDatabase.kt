package com.fmspcoding.notesapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fmspcoding.notesapp.data.local.entity.NoteEntity

@Database(
    entities = [NoteEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class NoteDatabase: RoomDatabase() {

    abstract val dao: NoteDao
}