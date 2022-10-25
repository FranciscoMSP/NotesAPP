package com.fmspcoding.notesapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fmspcoding.notesapp.core.Constants
import com.fmspcoding.notesapp.presentation.note_detail.NoteDetailScreen
import com.fmspcoding.notesapp.presentation.note_draw_canvas.NoteDrawScreen
import com.fmspcoding.notesapp.presentation.note_list.NoteListScreen
import com.fmspcoding.notesapp.presentation.ui.theme.NotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        //setContentView(R.layout.activity_main)
        setContent {
            NotesTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.NoteListScreen.route
                    ) {
                        composable(
                            route = Screen.NoteListScreen.route
                        ) {
                            NoteListScreen(navController = navController)
                        }
                        composable(
                            route = Screen.NoteDetailScreen.route + "/{noteId}", //?{${Constants.PARAM_DRAW_NAME}}
                            arguments = listOf(
                                navArgument("noteId") { type = NavType.IntType }
                                //navArgument(Constants.PARAM_DRAW_NAME) { defaultValue = "" }
                            )
                        ) { backStackEntry ->

//                            val drawName: String by backStackEntry
//                                .savedStateHandle
//                                .getStateFlow(Constants.PARAM_DRAW_NAME, "")
//                                .collectAsState()

                            val newNoteId: Long by backStackEntry
                                .savedStateHandle
                                .getStateFlow(Constants.PARAM_NEW_NOTE_ID, 0L)
                                .collectAsState()

                            NoteDetailScreen(navController = navController, newIdFromStack = newNoteId)
                        }
                        composable(
                            route = Screen.NoteDrawScreen.route + "/{noteId}",
                            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                        ) {
                            NoteDrawScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}