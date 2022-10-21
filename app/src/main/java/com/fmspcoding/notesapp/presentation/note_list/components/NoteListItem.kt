package com.fmspcoding.notesapp.presentation.note_list.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fmspcoding.notesapp.R
import com.fmspcoding.notesapp.domain.model.Note
import com.fmspcoding.notesapp.domain.model.NoteItem
import com.fmspcoding.notesapp.presentation.ui.spacing

@Composable
fun NoteListItem(
    note: NoteItem,
    modifier: Modifier = Modifier
) {

    Card(shape = RoundedCornerShape(8.dp),
        border = if(note.selected) BorderStroke(3.dp, MaterialTheme.colors.primary) else BorderStroke(1.dp, Color.Gray),
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
            //.clickable { onItemClick(note) }
            ,
        backgroundColor = MaterialTheme.colors.background
        ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.small)
        ) {
            AnimatedVisibility(visible = note.drawName.isNotEmpty()) {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    bitmap = note.drawBitMap.asImageBitmap(),
                    contentDescription = stringResource(R.string.draw)
                )
            }
            Text(
                text = note.title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
            if(note.checkItems.isEmpty()) {
                Text(
                    text = note.description,
                    style = MaterialTheme.typography.body1,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            } else {
                note.checkItems.forEach { item ->
                    CheckItemNoteList(item)
                }
            }
        }
    }
}