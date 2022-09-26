package com.fmspcoding.notesapp.presentation.note_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fmspcoding.notesapp.presentation.Screen
import com.fmspcoding.notesapp.presentation.note_list.components.NoteListItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteListScreen(
    navController: NavController,
    viewModel: NoteListViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    
    Scaffold(
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    cells = GridCells.Fixed(2)
                ) {
                    items(state.notes) { note ->
                        NoteListItem(
                            note = note,
                            onItemClick = {
                                navController.navigate(Screen.NoteDetailScreen.route + "/${note.id}")
                            }
                        )
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.NoteDetailScreen.route + "/${0}")
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),

                ) {
                Icon(Icons.Filled.Add, "")
            }
        }
    )

}