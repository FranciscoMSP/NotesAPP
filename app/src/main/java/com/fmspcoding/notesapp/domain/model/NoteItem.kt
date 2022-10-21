package com.fmspcoding.notesapp.domain.model

import android.graphics.Bitmap

data class NoteItem(
    val id: Int = 0,
    val title: String,
    val description: String,
    val checkItems: List<CheckItem>,
    val drawName: String,
    var drawBitMap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
    val selected: Boolean = false
)
