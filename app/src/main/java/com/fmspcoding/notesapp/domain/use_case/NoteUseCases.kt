package com.fmspcoding.notesapp.domain.use_case

data class NoteUseCases(
    val getNoteUseCase: GetNoteUseCase,
    val getNotesUseCase: GetNotesUseCase,
    val insertNoteUseCase: InsertNoteUseCase,
    val deleteNoteUseCase: DeleteNoteUseCase,
    val deleteNotesUseCase: DeleteNotesUseCase
)