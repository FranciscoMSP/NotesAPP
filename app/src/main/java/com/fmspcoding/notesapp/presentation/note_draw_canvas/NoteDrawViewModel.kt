package com.fmspcoding.notesapp.presentation.note_draw_canvas

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmspcoding.notesapp.core.Constants
import com.fmspcoding.notesapp.core.util.Resource
import com.fmspcoding.notesapp.core.util.generateRandomName
import com.fmspcoding.notesapp.core.util.loadImageFromInternalStorage
import com.fmspcoding.notesapp.core.util.saveImageToIntervalStorage
import com.fmspcoding.notesapp.domain.model.Note
import com.fmspcoding.notesapp.domain.use_case.NoteUseCases
import com.kpstv.compose.kapture.ScreenshotController
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _drawBitMap = mutableStateOf(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
    val drawBitMap: State<Bitmap> = _drawBitMap

    private val _state = mutableStateOf(NoteDrawState())
    val state: State<NoteDrawState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentNoteId = 0

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

                    val listIds: List<Long>? = result.data

                    var newId = 0L
                    if(listIds != null && listIds.isNotEmpty()) {
                        newId = listIds[0]
                    }

                    _eventFlow.emit(UiEvent.SaveNote(newId))
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

    private fun loadDraw(drawName: String) {
        viewModelScope.launch {
            val storageImage = loadImageFromInternalStorage(drawName, application.applicationContext)
            if(storageImage.name.isNotEmpty()) {
                _drawBitMap.value = storageImage.bitmap
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String): UiEvent()
        data class SaveNote(val id: Long): UiEvent()
        object GoBack: UiEvent()
    }

}