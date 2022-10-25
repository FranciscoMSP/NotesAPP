package com.fmspcoding.notesapp.presentation.note_draw_canvas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fmspcoding.notesapp.R
import com.fmspcoding.notesapp.presentation.note_detail.draw_canvas.gesture.dragMotionEvent
import com.fmspcoding.notesapp.presentation.note_draw_canvas.components.DrawOptionsMenu
import com.fmspcoding.notesapp.presentation.note_draw_canvas.components.OptionItem
import com.fmspcoding.notesapp.presentation.note_draw_canvas.gesture.MotionEvent
import com.fmspcoding.notesapp.presentation.note_draw_canvas.model.PathProperties
import com.fmspcoding.notesapp.presentation.ui.spacing
import com.fmspcoding.notesapp.presentation.ui.theme.gradientColors
import com.kpstv.compose.kapture.attachController
import com.kpstv.compose.kapture.rememberScreenshotController
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NoteDrawScreen(
    navController: NavController,
    viewModel: NoteDrawViewModel = hiltViewModel()
) {

    val paths = remember { mutableStateListOf<Pair<Path, PathProperties>>() }
    var currentPath by remember { mutableStateOf(Path()) }
    val pathsUndone = remember { mutableStateListOf<Pair<Path, PathProperties>>() }

    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    val drawMode by remember { mutableStateOf(DrawMode.Draw) }

    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    var currentPathProperty by remember { mutableStateOf(PathProperties()) }

    var colorListVisibility by remember { mutableStateOf(false) }
    var currentColor by remember { mutableStateOf(Color.Black) }

    var strokeSelectorVisibility by remember { mutableStateOf(false) }
    var currentStroke by remember { mutableStateOf(10f) }

    val scaffoldState = rememberScaffoldState()
    val screenshotController = rememberScreenshotController()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                NoteDrawViewModel.UiEvent.GoBack -> {
                    navController.navigateUp()
                }
                is NoteDrawViewModel.UiEvent.SaveNote -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "newNoteId",
                        event.id
                    )
                    navController.navigateUp()
                }
                is NoteDrawViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
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
                        if (navController.previousBackStackEntry != null) {
                            navController.navigateUp()
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, stringResource(id = R.string.back_icon))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.saveDraw(screenshotController)
                    }) {
                        Icon(
                            Icons.Filled.Save,
                            contentDescription = stringResource(id = R.string.save)
                        )
                    }
                }
            )
        },
        scaffoldState = scaffoldState
    ) { it ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Canvas(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White)
                .dragMotionEvent(
                    onDragStart = { pointerInputChange ->
                        motionEvent = MotionEvent.Down
                        currentPosition = pointerInputChange.position
                        pointerInputChange.consumeDownChange()

                    },
                    onDrag = { pointerInputChange ->
                        motionEvent = MotionEvent.Move
                        currentPosition = pointerInputChange.position

                        if (drawMode == DrawMode.Touch) {
                            val change = pointerInputChange.positionChange()

                            paths.forEach { entry ->
                                val path: Path = entry.first
                                path.translate(change)
                            }
                            currentPath.translate(change)
                        }
                        pointerInputChange.consumePositionChange()

                    },
                    onDragEnd = { pointerInputChange ->
                        motionEvent = MotionEvent.Up
                        pointerInputChange.consumeDownChange()
                    }
                )
                .attachController(screenshotController)
            ) {
                when (motionEvent) {
                    MotionEvent.Idle -> Unit
                    MotionEvent.Down -> {
                        if (drawMode != DrawMode.Touch) {
                            currentPath.moveTo(currentPosition.x, currentPosition.y)
                        }

                        previousPosition = currentPosition
                    }
                    MotionEvent.Move -> {
                        if (drawMode != DrawMode.Touch) {
                            currentPath.quadraticBezierTo(
                                previousPosition.x,
                                previousPosition.y,
                                (previousPosition.x + currentPosition.x) / 2,
                                (previousPosition.y + currentPosition.y) / 2

                            )
                        }

                        previousPosition = currentPosition
                    }
                    MotionEvent.Up -> {
                        if (drawMode != DrawMode.Touch) {
                            currentPath.lineTo(currentPosition.x, currentPosition.y)

                            // Pointer is up save current path
//                        paths[currentPath] = currentPathProperty
                            paths.add(Pair(currentPath, currentPathProperty))

                            // Since paths are keys for map, use new one for each key
                            // and have separate path for each down-move-up gesture cycle
                            currentPath = Path()

                            // Create new instance of path properties to have new path and properties
                            // only for the one currently being drawn
                            currentPathProperty = PathProperties(
                                strokeWidth = currentPathProperty.strokeWidth,
                                color = currentPathProperty.color,
                                strokeCap = currentPathProperty.strokeCap,
                                strokeJoin = currentPathProperty.strokeJoin,
                                eraseMode = currentPathProperty.eraseMode
                            )
                        }

                        // Since new path is drawn no need to store paths to undone
                        pathsUndone.clear()

                        // If we leave this state at MotionEvent.Up it causes current path to draw
                        // line from (0,0) if this composable recomposes when draw mode is changed
                        currentPosition = Offset.Unspecified
                        previousPosition = currentPosition
                        motionEvent = MotionEvent.Idle
                    }
                }

                with(drawContext.canvas.nativeCanvas) {

                    //drawBitmap(viewModel.drawBitMap.value, 0f, 0f, null)

                    val checkPoint = saveLayer(null, null)

                    //setBitmap(viewModel.drawBitMap.value)

                    paths.forEach {

                        val path = it.first
                        val property = it.second

                        if (!property.eraseMode) {
                            drawPath(
                                color = property.color,
                                path = path,
                                style = Stroke(
                                    width = property.strokeWidth,
                                    cap = property.strokeCap,
                                    join = property.strokeJoin
                                )
                            )
                        } else {

                            // Source
                            drawPath(
                                color = Color.Transparent,
                                path = path,
                                style = Stroke(
                                    width = currentPathProperty.strokeWidth,
                                    cap = currentPathProperty.strokeCap,
                                    join = currentPathProperty.strokeJoin
                                ),
                                blendMode = BlendMode.Clear
                            )
                        }
                    }

                    if (motionEvent != MotionEvent.Idle) {

                        if (!currentPathProperty.eraseMode) {
                            drawPath(
                                color = currentPathProperty.color,
                                path = currentPath,
                                style = Stroke(
                                    width = currentPathProperty.strokeWidth,
                                    cap = currentPathProperty.strokeCap,
                                    join = currentPathProperty.strokeJoin
                                )
                            )
                        } else {
                            drawPath(
                                color = Color.Transparent,
                                path = currentPath,
                                style = Stroke(
                                    width = currentPathProperty.strokeWidth,
                                    cap = currentPathProperty.strokeCap,
                                    join = currentPathProperty.strokeJoin
                                ),
                                blendMode = BlendMode.Clear
                            )
                        }
                    }
                    restoreToCount(checkPoint)
                }
            }
            DrawOptionsMenu(
                onColorClick = { colorListVisibility = !colorListVisibility },
                onStrokeClick = { strokeSelectorVisibility = !strokeSelectorVisibility },
                onUndoClick = {
                    if (paths.isNotEmpty()) {
                        val lastItem = paths.last()
                        val lastPath = lastItem.first
                        val lastPathProperty = lastItem.second
                        paths.remove(lastItem)

                        pathsUndone.add(Pair(lastPath, lastPathProperty))
                    }
                },
                onRedoClick = {
                    if (pathsUndone.isNotEmpty()) {
                        val lastPath = pathsUndone.last().first
                        val lastPathProperty = pathsUndone.last().second
                        pathsUndone.removeLast()
                        paths.add(Pair(lastPath, lastPathProperty))
                    }
                },
                undoVisibility = paths.isNotEmpty(),
                redoVisibility = pathsUndone.isNotEmpty(),
                currentColor = currentColor
            )
            AnimatedVisibility(visible = colorListVisibility) {
                Column {
                    //Divider(thickness = 1.dp, color = MaterialTheme.colors.onPrimary)
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(MaterialTheme.colors.primary)
                            .padding(horizontal = MaterialTheme.spacing.small),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(gradientColors) { color ->
                            OptionItem(
                                modifier = Modifier.size(if (currentColor == color) 36.dp else 24.dp),
                                resId = R.drawable.ic_baseline_circle_24,
                                description = stringResource(
                                    id = R.string.color_circle
                                ),
                                colorTint = color
                            ) {
                                if (currentColor != color) {
                                    currentPathProperty.color = color
                                }
                                currentColor = color
                                colorListVisibility = false
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(visible = strokeSelectorVisibility) {
                Slider(
                    modifier = Modifier.background(MaterialTheme.colors.primary),
                    value = currentStroke,
                    onValueChange = { currentStroke = it },
                    valueRange = 1f..20f,
                    onValueChangeFinished = {
                        // launch some business logic update with the state you hold
                        // viewModel.updateSelectedSliderValue(sliderPosition)
                        currentPathProperty.strokeWidth = currentStroke
                    },
                    //steps = 10,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White
                    )
                )
            }
        }
    }

}