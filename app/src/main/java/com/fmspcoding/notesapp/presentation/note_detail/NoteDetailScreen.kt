package com.fmspcoding.notesapp.presentation.note_detail

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fmspcoding.notesapp.presentation.note_detail.components.CheckItemNote
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NoteDetailScreen(
    navController: NavController,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val checkList = viewModel.checkList
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is NoteDetailViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is NoteDetailViewModel.UiEvent.SaveNote -> {
                    navController.navigateUp()
                }
                NoteDetailViewModel.UiEvent.DeleteNote -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.primary,
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        if(navController.previousBackStackEntry != null) {
                            navController.navigateUp()
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, "back_icon")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.onEvent(NoteDetailEvent.SaveNote)
                    }) {
                        Icon(Icons.Filled.Save, contentDescription = "Save")
                    }
                }
            )
        },
        bottomBar =  {
             BottomAppBar(backgroundColor = MaterialTheme.colors.primary) {
                 Spacer(Modifier.width(16.dp))
                 IconButton(onClick = { viewModel.onEvent(NoteDetailEvent.AddCheckList) }) {
                     Icon(Icons.Filled.CheckBox, contentDescription = "Add CheckBox")
                 }
                 Spacer(Modifier.width(16.dp))
                 if(viewModel.currentNoteId > 0) {
                     IconButton(onClick = { viewModel.onEvent(NoteDetailEvent.DeleteNote) }) {
                         Icon(Icons.Filled.Delete, contentDescription = "Delete Note")
                     }
                 }
             }
        },
        scaffoldState = scaffoldState,
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                ) {
                    TextField(
                        value = viewModel.title.value,
                        onValueChange = {
                            viewModel.onEvent(NoteDetailEvent.EnteredTitle(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        textStyle = MaterialTheme.typography.h5,
                        placeholder = {
                            Text(text = "Título")
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    AnimatedVisibility(
                        visible = !state.isCheckListVisible,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        TextField(
                            value = viewModel.description.value,
                            onValueChange = {
                                viewModel.onEvent(NoteDetailEvent.EnteredDescription(it))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            textStyle = MaterialTheme.typography.body1,
                            placeholder = {
                                Text(text = "Nota")
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }

                    AnimatedVisibility(
                        visible = state.isCheckListVisible,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            itemsIndexed(items = viewModel.checkList) { index, item ->
                                CheckItemNote(
                                    text = item.text,
                                    onTextValueChange = { viewModel.onEvent(NoteDetailEvent.EnteredItemListText(it, index)) },
                                    checked = item.isChecked,
                                    onChecked = { viewModel.onEvent(NoteDetailEvent.CheckedItem(it, index)) },
                                    onDelete = { viewModel.onEvent(NoteDetailEvent.DeleteItem(index)) })
                            }
                        }

                    }

                }
//                if(state.error.isNotBlank()) {
//                    Text(
//                        text = state.error,
//                        color = MaterialTheme.colors.error,
//                        textAlign = TextAlign.Center,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 20.dp)
//                            .align(Alignment.Center)
//                    )
//                }
                if(state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                
                if(viewModel.showDialog.value) {
                    AlertDialog(
                        onDismissRequest = { viewModel.onEvent(NoteDetailEvent.DialogDismiss) },
                        confirmButton = {
                            TextButton(onClick = { viewModel.onEvent(NoteDetailEvent.DialogConfirm) })
                            { Text(text = "OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.onEvent(NoteDetailEvent.DialogDismiss) })
                            { Text(text = "Cancel") }
                        },
                        title = { Text(text = "Eliminar Nota") },
                        text = { Text(text = "Prentende eliminar a nota atual?") }
                    )
                }
            }
        }
    )
}