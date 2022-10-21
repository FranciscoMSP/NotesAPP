package com.fmspcoding.notesapp.presentation.note_detail

import android.app.Application
import android.graphics.Bitmap
import android.provider.SyncStateContract
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmspcoding.notesapp.R
import com.fmspcoding.notesapp.core.Constants
import com.fmspcoding.notesapp.core.util.Resource
import com.fmspcoding.notesapp.core.util.UiText
import com.fmspcoding.notesapp.core.util.loadImageFromInternalStorage
import com.fmspcoding.notesapp.domain.model.CheckItem
import com.fmspcoding.notesapp.domain.model.DetailOption
import com.fmspcoding.notesapp.domain.model.Note
import com.fmspcoding.notesapp.domain.use_case.*
import com.fmspcoding.notesapp.presentation.note_list.NoteListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val application: Application,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    val title = savedStateHandle.getStateFlow("title", "")
    val description = savedStateHandle.getStateFlow("description", "")
    val drawName = savedStateHandle.getStateFlow("drawName", "")

    private val _checkList = mutableStateListOf<CheckItem>()
    val checkList: List<CheckItem> = _checkList

    private val _drawBitMap = mutableStateOf(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
    val drawBitMap: State<Bitmap> = _drawBitMap

    private val _showDialog = mutableStateOf(false)
    val showDialog: State<Boolean> = _showDialog

    private val _state = mutableStateOf(NoteDetailState())
    val state: State<NoteDetailState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _detailOptions = mutableStateListOf<DetailOption>()
    val detailOptions: List<DetailOption> = _detailOptions

    var currentNoteId: Int = 0

    init {
        savedStateHandle.get<Int>(Constants.PARAM_NOTE_ID)?.let { noteId ->
            if(noteId > 0) {
                currentNoteId = noteId
                getNote(noteId)
            }
        }
    }

    fun onEvent(event: NoteDetailEvent) {
        when(event) {
            is NoteDetailEvent.EnteredTitle -> {
                //_title.value = event.value
                savedStateHandle["title"] = event.value
            }
            is NoteDetailEvent.EnteredDescription -> {
               savedStateHandle["description"] = event.value
            }
            NoteDetailEvent.SaveNote -> {
                insertNote()
            }
            NoteDetailEvent.AddCheckList -> {
                val check = CheckItem()
                _checkList.add(check)
                _state.value = _state.value.copy(noteDetailMode = NoteDetailMode.CheckList)
            }
            NoteDetailEvent.AddItemToList -> {
                val check = CheckItem()
                _checkList.add(check)
            }
            is NoteDetailEvent.CheckedItem -> {
                _checkList[event.index] = _checkList[event.index].copy(
                    isChecked = event.value
                )
            }
            is NoteDetailEvent.DeleteItem -> {
                _checkList.removeAt(event.index)
                if(_checkList.size == 0) {
                    _state.value = _state.value.copy(noteDetailMode = NoteDetailMode.Default)
                }
            }
            NoteDetailEvent.DeleteNote -> {
                _showDialog.value = true
            }
            is NoteDetailEvent.EnteredItemListText -> {
                _checkList[event.index] = _checkList[event.index].copy(
                    text = event.value
                )
            }
            NoteDetailEvent.DialogConfirm -> {
                _showDialog.value = false
                deleteNote(_state.value.note!!)
            }
            NoteDetailEvent.DialogDismiss -> {
                _showDialog.value = false
            }
            NoteDetailEvent.OpenDetailMenu -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ShowBottomSheet)
                }
            }
            is NoteDetailEvent.DetailItemClick -> {
                when(event.noteMode) {
                    NoteDetailMode.Default -> {}
                    NoteDetailMode.CheckList -> {
                        val check = CheckItem()
                        _checkList.add(check)
                        _state.value = _state.value.copy(noteDetailMode = NoteDetailMode.CheckList)
                    }
                    NoteDetailMode.DrawCanvas -> {
                        //_state.value = _state.value.copy(noteDetailMode = NoteDetailMode.DrawCanvas)
                        viewModelScope.launch {
                            _eventFlow.emit(UiEvent.GoToDrawScreen)
                        }
                    }
                }
            }
        }
    }

    fun getDetailList() {
        _detailOptions.clear()
        _detailOptions.add(DetailOption(R.string.draw, R.drawable.ic_outline_brush_24, NoteDetailMode.DrawCanvas))
        if(_state.value.noteDetailMode != NoteDetailMode.CheckList) {
            _detailOptions.add(DetailOption(R.string.check_list, R.drawable.ic_outline_check_box_24, NoteDetailMode.CheckList))

        }
    }

    private fun getNote(noteId: Int) {
        noteUseCases.getNoteUseCase(noteId).onEach { result ->
            when(result) {
                is Resource.Loading -> {
                    _state.value = NoteDetailState(isLoading = true)
                }
                is Resource.Success ->  {
                    _state.value = NoteDetailState(
                        note = result.data,
                        noteDetailMode = if (result.data?.checkItems!!.isNotEmpty()) NoteDetailMode.CheckList else NoteDetailMode.Default
                    )
                    savedStateHandle["title"] = _state.value.note!!.title
                    savedStateHandle["description"] = _state.value.note!!.description
                    _checkList.addAll(result.data.checkItems)

                    savedStateHandle["drawName"] = result.data.drawName

                    if(result.data.drawName.isNotEmpty()) {
                        loadDraw(result.data.drawName)
                    }
                }
                is Resource.Error -> {
                    _state.value = NoteDetailState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun insertNote() {
        val note = Note(
            id = currentNoteId,
            title = title.value,
            description = description.value,
            checkItems = checkList,
            drawName = drawName.value
        )
        noteUseCases.insertNoteUseCase(note).onEach { result ->
            when(result) {
                is Resource.Loading -> {
                    _state.value = NoteDetailState(isLoading = true)
                }
                is Resource.Success ->  {
                    _state.value = NoteDetailState(isLoading = false)
                    _eventFlow.emit(UiEvent.SaveNote)
                }
                is Resource.Error -> {
                    _state.value = NoteDetailState(
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

    private fun deleteNote(note: Note) {
        noteUseCases.deleteNoteUseCase(note).onEach { result ->
            when(result) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.DeleteNote)
                }
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(
                            message = result.message ?: "Couldn't save note"
                        )
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun loadDraw(drawName: String, fromBackStack: Boolean = false) {
        if(fromBackStack) {
            savedStateHandle["drawName"] = drawName
        }

        viewModelScope.launch {
            val storageImage = loadImageFromInternalStorage(drawName, application.applicationContext)
            if(storageImage.name.isNotEmpty()) {
                _drawBitMap.value = storageImage.bitmap
            }
        }
    }

    fun showDrawName(name: String) {
        println("Desenho $name")
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String): UiEvent()
        object SaveNote: UiEvent()
        object DeleteNote: UiEvent()
        object ShowBottomSheet: UiEvent()
        object GoToDrawScreen: UiEvent()
    }
}
