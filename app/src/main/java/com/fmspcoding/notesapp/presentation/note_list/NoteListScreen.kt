package com.fmspcoding.notesapp.presentation.note_list

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fmspcoding.notesapp.R
import com.fmspcoding.notesapp.presentation.Screen
import com.fmspcoding.notesapp.presentation.note_list.components.NoteListItem
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteListScreen(
    navController: NavController,
    viewModel: NoteListViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val countSelected = viewModel.countSelected.value
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is NoteListViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is NoteListViewModel.UiEvent.ShowSnackbarAction -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionText,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        event.onClickAction()
                    }
                    if (result == SnackbarResult.Dismissed) {
                        viewModel.deleteDrawings()
                    }
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.NoteDetailScreen.route + "/${0}") {
                        popUpTo(Screen.NoteListScreen.route)
                    }
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.background,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),

                ) {
                Icon(Icons.Filled.Add, "")
            }
        }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it)
        ) {
            Column {
                AnimatedVisibility(
                    visible = countSelected > 0,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(color = MaterialTheme.colors.primary)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                viewModel.onEvent(NoteListEvent.CancelDelete)
                            }) {
                                Icon(
                                    Icons.Filled.Close,
                                    stringResource(id = R.string.close_delete_menu),
                                    tint = MaterialTheme.colors.background
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = countSelected.toString(),
                                style = MaterialTheme.typography.body1,
                                fontSize = 20.sp,
                                color = MaterialTheme.colors.background
                            )
                        }
                        IconButton(onClick = {
                            viewModel.onEvent(NoteListEvent.DeleteNotes)
                        }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = stringResource(id = R.string.delete_note),
                                tint = MaterialTheme.colors.background
                            )
                        }
                    }
                }

                LazyColumn {
                    item {
                        StaggeredVerticalGrid(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp, vertical = 16.dp),
                            maxColumnWidth = 220.dp
                        ) {
                            viewModel.noteList.forEachIndexed { index, note ->
                                NoteListItem(
                                    note = note,
                                    modifier = Modifier
                                        .combinedClickable(
                                            onClick = {
                                                if (countSelected > 0) {
                                                    viewModel.onEvent(
                                                        NoteListEvent.SelectItem(
                                                            note.selected,
                                                            index
                                                        )
                                                    )
                                                } else {
                                                    navController.navigate(Screen.NoteDetailScreen.route + "/${note.id}")
                                                }
                                            },
                                            onLongClick = {
                                                viewModel.onEvent(
                                                    NoteListEvent.SelectItem(
                                                        note.selected,
                                                        index
                                                    )
                                                )
                                            }
                                        )
                                )
                            }
                        }
                    }
                }
            }

            if (state.error.isNotBlank()) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colors.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .align(Alignment.Center)
                )
            }
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

}