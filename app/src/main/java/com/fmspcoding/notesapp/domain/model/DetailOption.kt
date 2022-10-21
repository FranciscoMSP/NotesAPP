package com.fmspcoding.notesapp.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.fmspcoding.notesapp.presentation.note_detail.NoteDetailMode

data class DetailOption(
    @StringRes val textResId: Int,
    @DrawableRes val resId: Int,
    val mode: NoteDetailMode
)
