package com.fmspcoding.notesapp.presentation.note_list.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fmspcoding.notesapp.domain.model.CheckItem

@Composable
fun CheckItemNoteList(
    item: CheckItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(item.isChecked) {
            Icon(Icons.Filled.CheckBox, contentDescription = "CheckBox Checked")
        } else {
            Icon(Icons.Filled.CheckBoxOutlineBlank, contentDescription = "CheckBox Blank")
        }
        Spacer(modifier = modifier.width(4.dp))
        Text(
            text = item.text,
            style = MaterialTheme.typography.body1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}