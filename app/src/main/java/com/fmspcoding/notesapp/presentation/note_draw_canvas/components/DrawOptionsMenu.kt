package com.fmspcoding.notesapp.presentation.note_draw_canvas.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fmspcoding.notesapp.R
import com.fmspcoding.notesapp.presentation.note_draw_canvas.DrawOptionState
import com.fmspcoding.notesapp.presentation.ui.spacing

@Composable
fun DrawOptionsMenu(
    onColorClick: () -> Unit,
    onStrokeClick: () -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    undoVisibility: Boolean,
    redoVisibility: Boolean,
    currentColor: Color
) {
    Box {

//        var width by remember { mutableStateOf(0f) }
//        var posUndo by remember { mutableStateOf(0f) }
//        var currentIndex by remember { mutableStateOf(0) }
//        val offsetAnim by animateFloatAsState(
//            targetValue = when (currentIndex) {
//                1 -> width / 2f - (width / 8f)
//                2 -> width / 2f + (width / 8f)//with(LocalDensity.current){100.dp.toPx()}
//                3 -> width * (0.75f) + with(LocalDensity.current){24.dp.toPx()}
//                else -> 0f + width / 8f
//            }
//        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
//                .onGloballyPositioned {
//                    width = it.size.width.toFloat()
//                }
                .background(MaterialTheme.colors.primary),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OptionItem(
                resId = R.drawable.ic_baseline_undo_24,
                description = stringResource(id = R.string.undo),
                colorTint = if(undoVisibility) Color.White else Color.LightGray
            ) {
                onUndoClick()
            }
            OptionItem(
                resId = R.drawable.ic_baseline_redo_24,
                description = stringResource(id = R.string.redo),
                colorTint = if(redoVisibility) Color.White else Color.LightGray
            ) {
                onRedoClick()
            }
            OptionItem(
                resId = R.drawable.ic_baseline_circle_24,
                description = stringResource(id = R.string.color_picker),
                colorTint = currentColor
            ) {
                onColorClick()
            }
            OptionItem(
                resId = R.drawable.ic_baseline_show_chart_24,
                description = stringResource(id = R.string.stroke_picker),
                colorTint = Color.White
            ) {
                onStrokeClick()
            }
        }
//        Box(
//            modifier = Modifier
//                .width(24.dp)
//                .height(3.dp)
//                .offset(with(LocalDensity.current) { offsetAnim.toDp() }, 52.dp)
//                .clip(RoundedCornerShape(5.dp))
//                .background(Color.White)
//        )
    }
}

@Composable
fun OptionItem(
    modifier: Modifier = Modifier,
    @DrawableRes resId: Int,
    description: String,
    colorTint: Color,
    onClick: () -> Unit
) {
    IconButton(onClick = { onClick() }) {
        Icon(modifier = modifier, painter = painterResource(id = resId), contentDescription = description, tint = colorTint)
    }
}

//Animate Between tabs
@Composable
fun OptionsTabBar(
    tabPage: DrawOptionState,
    onTabSelected: (tabPage: DrawOptionState) -> Unit
) {
    TabRow(
        selectedTabIndex = tabPage.ordinal,
        backgroundColor = MaterialTheme.colors.primary,
        indicator = { tabPositions ->
            OptionsTabIndicator(tabPositions, tabPage)
        }
    ) {
        OptionItem(
            resId = R.drawable.ic_baseline_undo_24,
            description = stringResource(id = R.string.undo),
            colorTint = if(tabPage == DrawOptionState.Undo) Color.White else Color.LightGray
        ) {
           onTabSelected(DrawOptionState.Undo)
        }
        OptionItem(
            resId = R.drawable.ic_baseline_redo_24,
            description = stringResource(id = R.string.redo),
            colorTint = if(tabPage == DrawOptionState.Redo) Color.White else Color.LightGray
        ) {
            onTabSelected(DrawOptionState.Redo)
        }
        OptionItem(
            resId = R.drawable.ic_baseline_circle_24,
            description = stringResource(id = R.string.color_picker),
            colorTint = Color.Red
        ) {
            onTabSelected(DrawOptionState.Color)
        }
        OptionItem(
            resId = R.drawable.ic_baseline_show_chart_24,
            description = stringResource(id = R.string.stroke_picker),
            colorTint = if(tabPage == DrawOptionState.Stroke) Color.White else Color.LightGray
        ) {
            onTabSelected(DrawOptionState.Stroke)
        }
    }
}

@Composable
private fun OptionsTabIndicator(
    tabPositions: List<TabPosition>,
    currentState: DrawOptionState
) {
    val transition = updateTransition(
        currentState,
        label = "Index indicator"
    )
    val indicatorLeft by transition.animateDp(
        transitionSpec = {
            if (DrawOptionState.Undo isTransitioningTo DrawOptionState.Redo) {
                // Indicator moves to the right.
                // Low stiffness spring for the left edge so it moves slower than the right edge.
                spring(stiffness = Spring.StiffnessVeryLow)
            } else {
                // Indicator moves to the left.
                // Medium stiffness spring for the left edge so it moves faster than the right edge.
                spring(stiffness = Spring.StiffnessMedium)
            }
        },
        label = "Indicator left"
    ) { page ->
        tabPositions[page.ordinal].left
    }
    val indicatorRight by transition.animateDp(
        transitionSpec = {
            if (DrawOptionState.Redo isTransitioningTo DrawOptionState.Undo) {
                // Indicator moves to the right
                // Medium stiffness spring for the right edge so it moves faster than the left edge.
                spring(stiffness = Spring.StiffnessMedium)
            } else {
                // Indicator moves to the left.
                // Low stiffness spring for the right edge so it moves slower than the left edge.
                spring(stiffness = Spring.StiffnessVeryLow)
            }
        },
        label = "Indicator right"
    ) { page ->
        tabPositions[page.ordinal].right
    }

    Box(
        Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.BottomStart)
            .offset(x = indicatorLeft)
            .width(indicatorRight - indicatorLeft)
            .padding(4.dp)
            .fillMaxSize()
            .border(
                BorderStroke(2.dp, Color.Red),
                RoundedCornerShape(4.dp)
            )
    )
}