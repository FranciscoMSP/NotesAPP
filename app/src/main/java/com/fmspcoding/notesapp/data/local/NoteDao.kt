package com.fmspcoding.notesapp.data.local

import androidx.room.*
import com.fmspcoding.notesapp.data.local.entity.NoteEntity

@Dao
interface NoteDao {
    @Query("SELECT * FROM noteentity")
    suspend fun getNotes(): List<NoteEntity>

    @Query("SELECT * FROM noteentity WHERE Id = :noteId")
    suspend fun getNote(noteId: Int): NoteEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Query("DELETE FROM noteentity WHERE Id = :noteId")
    suspend fun deleteNote(noteId: Int)

    @Update
    suspend fun updateNote(note: NoteEntity)
}