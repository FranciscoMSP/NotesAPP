package com.fmspcoding.notesapp.presentation

sealed class Screen(val route: String) {
    object NoteListScreen: Screen("note_list_screen")
    object NoteDetailScreen: Screen("note_detail_screen")
}