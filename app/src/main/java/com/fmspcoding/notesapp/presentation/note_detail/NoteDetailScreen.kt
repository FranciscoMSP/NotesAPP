package com.fmspcoding.notesapp.presentation.note_detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fmspcoding.notesapp.R
import com.fmspcoding.notesapp.presentation.Screen
import com.fmspcoding.notesapp.presentation.note_detail.components.CheckItemNote
import com.fmspcoding.notesapp.presentation.note_detail.components.NoteDetailOptionsBottomSheet
import com.fmspcoding.notesapp.presentation.ui.spacing
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteDetailScreen(
    navController: NavController,
    drawNameFromStack: String,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val scaffoldState = rememberScaffoldState()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded }
    )
    val coroutineScope = rememberCoroutineScope()


    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val drawName by viewModel.drawName.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
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
                NoteDetailViewModel.UiEvent.ShowBottomSheet -> {
                    viewModel.getDetailList()
                    sheetState.show()
                }
                NoteDetailViewModel.UiEvent.GoToDrawScreen -> {
                    navController.navigate(Screen.NoteDrawScreen.route + "/${viewModel.currentNoteId}")
                }
            }
        }
    }
    
    LaunchedEffect(key1 = drawNameFromStack) {
        if(drawNameFromStack.isNotEmpty()) {
            viewModel.loadDraw(drawNameFromStack, true)
            viewModel.showDrawName(drawNameFromStack)
        }
    }

    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            NoteDetailOptionsBottomSheet(
                detailOptions = viewModel.detailOptions,
                detailClick = {
                    viewModel.onEvent(NoteDetailEvent.DetailItemClick(it))
                    coroutineScope.launch {
                        sheetState.hide()
                    }
                }
            )
        },
        sheetState = sheetState,
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.primary,
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = {
                            if (navController.previousBackStackEntry != null) {
                                navController.navigateUp()
                            }
                        }) {
                            Icon(Icons.Filled.ArrowBack, stringResource(id = R.string.back_icon))
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.onEvent(NoteDetailEvent.SaveNote)
                        }) {
                            Icon(
                                Icons.Filled.Save,
                                contentDescription = stringResource(id = R.string.save)
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar(backgroundColor = MaterialTheme.colors.primary) {
                    //Spacer(Modifier.width(MaterialTheme.spacing.medium))
                    IconButton(onClick = { viewModel.onEvent(NoteDetailEvent.OpenDetailMenu) }) {
                        Icon(
                            Icons.Outlined.AddBox,
                            contentDescription = stringResource(id = R.string.open_detail_menu)
                        )
                    }
                    Spacer(Modifier.width(MaterialTheme.spacing.medium))
                    if (viewModel.currentNoteId > 0) {
                        IconButton(onClick = { viewModel.onEvent(NoteDetailEvent.DeleteNote) }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = stringResource(id = R.string.delete_note)
                            )
                        }
                    }
                }
            },
            scaffoldState = scaffoldState,
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(it)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(MaterialTheme.spacing.medium)
                ) {
                    AnimatedVisibility(visible = drawName.isNotEmpty()) {
                        Image(
                            modifier = Modifier.fillMaxWidth(),
                            bitmap = viewModel.drawBitMap.value.asImageBitmap(),
                            contentDescription = stringResource(R.string.draw)
                        )
                    }


                    TextField(
                        value = title,
                        onValueChange = {
                            viewModel.onEvent(NoteDetailEvent.EnteredTitle(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        textStyle = MaterialTheme.typography.h5,
                        placeholder = {
                            Text(text = stringResource(id = R.string.title))
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                    AnimatedVisibility(
                        visible = state.noteDetailMode == NoteDetailMode.Default,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        TextField(
                            value = description,
                            onValueChange = {
                                viewModel.onEvent(NoteDetailEvent.EnteredDescription(it))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            textStyle = MaterialTheme.typography.body1,
                            placeholder = {
                                Text(text = stringResource(id = R.string.note))
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }

                    AnimatedVisibility(
                        visible = state.noteDetailMode == NoteDetailMode.CheckList,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            itemsIndexed(items = viewModel.checkList) { index, item ->
                                CheckItemNote(
                                    text = item.text,
                                    onTextValueChange = {
                                        viewModel.onEvent(
                                            NoteDetailEvent.EnteredItemListText(
                                                it,
                                                index
                                            )
                                        )
                                    },
                                    checked = item.isChecked,
                                    onChecked = {
                                        viewModel.onEvent(
                                            NoteDetailEvent.CheckedItem(
                                                it,
                                                index
                                            )
                                        )
                                    },
                                    onDelete = { viewModel.onEvent(NoteDetailEvent.DeleteItem(index)) })
                            }
                            item {
                                Row(modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        viewModel.onEvent(NoteDetailEvent.AddItemToList)
                                    }
                                    .padding(MaterialTheme.spacing.medium)
                                ) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = stringResource(id = R.string.add_note)
                                    )
                                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                                    Text(
                                        text = stringResource(id = R.string.add_item_to_list),
                                        style = MaterialTheme.typography.body1
                                    )
                                }
                            }
                        }

                    }
                }

                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                if (viewModel.showDialog.value) {
                    AlertDialog(
                        onDismissRequest = { viewModel.onEvent(NoteDetailEvent.DialogDismiss) },
                        confirmButton = {
                            TextButton(onClick = { viewModel.onEvent(NoteDetailEvent.DialogConfirm) })
                            { Text(text = stringResource(R.string.ok)) }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.onEvent(NoteDetailEvent.DialogDismiss) })
                            { Text(text = stringResource(id = R.string.cancel)) }
                        },
                        title = { Text(text = stringResource(id = R.string.delete_note)) },
                        text = { Text(text = stringResource(id = R.string.delete_current_note_question)) }
                    )
                }
            }
        }
    }
}