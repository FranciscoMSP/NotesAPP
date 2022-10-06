package com.fmspcoding.notesapp.presentation.note_detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun CheckItemNote(
    modifier: Modifier = Modifier,
    text: String = "",
    onTextValueChange: (String) -> Unit,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onChecked,
            colors = CheckboxDefaults.colors(MaterialTheme.colors.primary)
        )
        Spacer(modifier = modifier.width(8.dp))
        TextField(
            modifier = Modifier.weight(1f),
            value = text,
            onValueChange = { onTextValueChange(it) },
            textStyle = MaterialTheme.typography.body1,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = modifier.width(8.dp))
        IconButton(onClick = { onDelete() }) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Delete",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}