package com.fmspcoding.notesapp.presentation.note_detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fmspcoding.notesapp.R
import com.fmspcoding.notesapp.presentation.Screen

@Composable
fun NoteDetailScreen(
    navController: NavController,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    //val scaffoldState = rememberScaffoldState()

    Scaffold(
        topBar = {
            TopAppBar(
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
                        viewModel.insertNote()
                    }) {
                        Icon(Icons.Filled.Save, contentDescription = "Save")
                    }
                }
            )
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                ) {
                    TextField(
                        value = viewModel.title.value,
                        onValueChange = viewModel::onTitle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        placeholder = {
                            Text(text = "Título")
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = viewModel.description.value,
                        onValueChange = viewModel::onDescription,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        placeholder = {
                            Text(text = "Nota")
                        }
                    )
                }
                if(state.error.isNotBlank()) {
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
                if(state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                if(state.isSaved) {
                    if(navController.previousBackStackEntry != null) {
                        navController.navigateUp()
                    }
                }
            }
        }
    )
}