package com.fmspcoding.notesapp.presentation.note_list.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fmspcoding.notesapp.domain.model.Note

@Composable
fun NoteListItem(
    note: Note,
    onItemClick: (Note) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onItemClick(note) }
            .padding(16.dp)
            .border(1.dp, Color.Gray)
    ) {
        Text(
            text = note.title,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = note.description,
            style = MaterialTheme.typography.body1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )
    }
}