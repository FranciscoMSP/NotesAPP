package com.fmspcoding.notesapp.presentation.note_detail

import android.provider.SyncStateContract
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmspcoding.notesapp.core.Constants
import com.fmspcoding.notesapp.core.util.Resource
import com.fmspcoding.notesapp.domain.model.CheckItem
import com.fmspcoding.notesapp.domain.model.Note
import com.fmspcoding.notesapp.domain.use_case.*
import com.fmspcoding.notesapp.presentation.note_list.NoteListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _title = mutableStateOf("")
    val title: State<String> = _title

    private val _description = mutableStateOf("")
    val description: State<String> = _description

    private val _checkList = mutableStateListOf<CheckItem>()
    val checkList: List<CheckItem> = _checkList

    private val _showDialog = mutableStateOf(false)
    val showDialog: State<Boolean> = _showDialog

    private val _state = mutableStateOf(NoteDetailState())
    val state: State<NoteDetailState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

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
                _title.value = event.value
            }
            is NoteDetailEvent.EnteredDescription -> {
                _description.value = event.value
            }
            NoteDetailEvent.SaveNote -> {
                insertNote()
            }
            NoteDetailEvent.AddCheckList -> {
                val check = CheckItem()
                _checkList.add(check)
                _state.value = _state.value.copy(isCheckListVisible = true)
            }
            NoteDetailEvent.AddItemToList -> {

            }
            is NoteDetailEvent.CheckedItem -> {
                _checkList[event.index] = _checkList[event.index].copy(
                    isChecked = event.value
                )
            }
            is NoteDetailEvent.DeleteItem -> {
                _checkList.removeAt(event.index)
                if(_checkList.size == 0) {
                    _state.value = _state.value.copy(isCheckListVisible = false)
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
        }
    }

    private fun getNote(noteId: Int) {
        noteUseCases.getNoteUseCase(noteId).onEach { result ->
            when(result) {
                is Resource.Loading -> {
                    _state.value = NoteDetailState(isLoading = true)
                }
                is Resource.Success ->  {
                    _state.value = NoteDetailState(note = result.data, isCheckListVisible = result.data?.checkItems!!.isNotEmpty())
                    _title.value = _state.value.note!!.title
                    _description.value = _state.value.note!!.description
                    _checkList.addAll(result.data.checkItems)
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
        val note = Note(id = currentNoteId, title = title.value, description = description.value, checkItems = checkList)
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

    sealed class UiEvent {
        data class ShowSnackbar(val message: String): UiEvent()
        object SaveNote: UiEvent()
        object DeleteNote: UiEvent()
    }
}
