package com.fmspcoding.notesapp.presentation.note_draw_canvas

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmspcoding.notesapp.core.Constants
import com.fmspcoding.notesapp.core.util.Resource
import com.fmspcoding.notesapp.core.util.generateRandomName
import com.fmspcoding.notesapp.core.util.saveImageToIntervalStorage
import com.fmspcoding.notesapp.domain.model.Note
import com.fmspcoding.notesapp.domain.use_case.NoteUseCases
import com.fmspcoding.notesapp.presentation.note_detail.NoteDetailMode
import com.fmspcoding.notesapp.presentation.note_detail.NoteDetailState
import com.fmspcoding.notesapp.presentation.note_detail.NoteDetailViewModel
import com.kpstv.compose.kapture.ScreenshotController
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NoteDrawViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val application: Application,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(NoteDrawState())
    val state: State<NoteDrawState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var currentNoteId = 0

    init {
        savedStateHandle.get<Int>(Constants.PARAM_NOTE_ID)?.let { noteId ->
            if (noteId > 0) {
                currentNoteId = noteId
                getNote(noteId)
            }
        }
    }

    fun saveDraw(screenshotController: ScreenshotController) {
        viewModelScope.launch {
            val bitmap: Result<Bitmap> = screenshotController.captureToBitmap()

            bitmap.fold(
                onSuccess = { bmp ->
                    val imageName = "image-" + generateRandomName()
                    try {
                        val saved = saveImageToIntervalStorage(imageName, bitmap = bmp, application.applicationContext)
                        if(saved) {
                            insertNote("$imageName.png")
                        } else {
                            _eventFlow.emit(UiEvent.ShowSnackbar("Couldn't save draw."))
                        }
                    } catch (e: IOException) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("Couldn't save draw."))
                    }
                },
                onFailure = {
                    //Show Error
                }
            )
        }
    }

    private fun getNote(noteId: Int) {
        noteUseCases.getNoteUseCase(noteId).onEach { result ->
            when(result) {
                is Resource.Loading -> {
                    _state.value = NoteDrawState(isLoading = true)
                }
                is Resource.Success ->  {
                    _state.value = NoteDrawState(
                        note = result.data,
                    )
                }
                is Resource.Error -> {
                    _state.value = NoteDrawState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun insertNote(imageName: String) {
        val note = Note(
            id = currentNoteId,
            title = if(state.value.note == null) "" else state.value.note!!.title,
            description = if(state.value.note == null) "" else state.value.note!!.description,
            checkItems = if(state.value.note == null) emptyList() else state.value.note!!.checkItems,
            drawName = imageName
        )
        noteUseCases.insertNoteUseCase(note).onEach { result ->
            when(result) {
                is Resource.Loading -> {
                    _state.value = NoteDrawState(isLoading = true)
                }
                is Resource.Success ->  {
                    _state.value = NoteDrawState(isLoading = false)
                    _eventFlow.emit(UiEvent.SaveNote(imageName))
                }
                is Resource.Error -> {
                    _state.value = NoteDrawState(
                        error = result.message ?: "An unexpected error occured",
                        isLoading = false
                    )
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(
                            message = result.message ?: "Couldn't save note"
                        )
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String): UiEvent()
        data class SaveNote(val drawName: String): UiEvent()
        object GoBack: UiEvent()
    }

}