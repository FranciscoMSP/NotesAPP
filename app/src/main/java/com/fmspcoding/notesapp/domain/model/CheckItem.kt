package com.fmspcoding.notesapp.domain.model

data class CheckItem(
    val text: String = "",
    val isChecked: Boolean = false,
    val order: Int = 0
)
