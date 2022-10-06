package com.fmspcoding.notesapp.data.local

import androidx.room.*
import com.fmspcoding.notesapp.domain.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE Id = :noteId")
    suspend fun getNote(noteId: Int): Note

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(vararg note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM note WHERE id in (:idList)")
    suspend fun deleteNotes(idList: List<Int>)
}