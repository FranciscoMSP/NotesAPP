package com.fmspcoding.notesapp.presentation.note_detail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fmspcoding.notesapp.core.util.UiText
import com.fmspcoding.notesapp.domain.model.DetailOption
import com.fmspcoding.notesapp.presentation.note_detail.NoteDetailMode
import com.fmspcoding.notesapp.presentation.ui.spacing

@Composable
fun NoteDetailOptionsBottomSheet(
    modifier: Modifier = Modifier,
    detailOptions: List<DetailOption>,
    detailClick: (NoteDetailMode) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary)
            .padding(bottom = MaterialTheme.spacing.small)
    ) {
        items(detailOptions) { item ->
            Row(modifier = modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable { detailClick(item.mode) }
                .padding(
                    horizontal = MaterialTheme.spacing.small
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = item.resId),
                    contentDescription = UiText.StringResource(item.textResId, "").asString(),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colors.background)
                )
                Spacer(modifier = modifier.width(MaterialTheme.spacing.medium))
                Text(
                    text = UiText.StringResource(item.textResId, "").asString(),
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.background,
                    fontSize = 18.sp
                )
            }
        }
    }
}