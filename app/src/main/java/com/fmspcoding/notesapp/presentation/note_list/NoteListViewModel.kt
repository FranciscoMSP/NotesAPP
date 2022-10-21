package com.fmspcoding.notesapp.presentation.note_list

import android.app.Application
import android.content.res.Resources
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmspcoding.notesapp.R
import com.fmspcoding.notesapp.core.util.Resource
import com.fmspcoding.notesapp.core.util.loadImageFromInternalStorage
import com.fmspcoding.notesapp.domain.model.CheckItem
import com.fmspcoding.notesapp.domain.model.Note
import com.fmspcoding.notesapp.domain.model.NoteItem
import com.fmspcoding.notesapp.domain.use_case.NoteUseCases
import com.fmspcoding.notesapp.presentation.note_detail.NoteDetailState
import com.fmspcoding.notesapp.presentation.note_detail.NoteDetailViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val application: Application
) : ViewModel() {

    private val _state = mutableStateOf(NoteListState())
    val state: State<NoteListState> = _state

    private val _noteList = mutableStateListOf<NoteItem>()
    val noteList: List<NoteItem> = _noteList

    private val _countSelected = mutableStateOf(0)
    val countSelected: State<Int> = _countSelected

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var recentlyDeletedNotes: List<Note> = emptyList()

    private var getNotesJob: Job? = null

    init {
        getNotes()
    }

    fun onEvent(event: NoteListEvent) {
        when (event) {
            NoteListEvent.DeleteNotes -> {
                val listIds = _noteList.filter { it.selected }.map { it.id }
                val listNotes = state.value.notes.filter { it.id in listIds }

                recentlyDeletedNotes = listNotes
                _countSelected.value = 0
                deleteNotes(listIds)
            }
            is NoteListEvent.SelectItem -> {
                _noteList[event.index] = _noteList[event.index].copy(
                    selected = !event.currentValue
                )

                if (!event.currentValue) _countSelected.value++ else _countSelected.value--
            }
            NoteListEvent.CancelDelete -> {
                for (i in 0 until _noteList.size) {
                    if (_noteList[i].selected) {
                        _noteList[i] = noteList[i].copy(selected = false)
                    }
                }
                _countSelected.value = 0
            }
            NoteListEvent.RestoreNotes -> {
                insertNotes(recentlyDeletedNotes)
            }
        }
    }

    private fun getNotes() {
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getNotesUseCase()
            .onEach { notes ->
                saveNewList(notes)
                getDrawings()
            }
            .launchIn(viewModelScope)
    }

    private fun saveNewList(list: List<Note>) {
        _state.value = state.value.copy(
            notes = list
        )
        _noteList.clear()
        _noteList.addAll(list.map { it.toNoteItem() })
    }

    private fun insertNotes(list: List<Note>) {
        noteUseCases.insertNoteUseCase(*list.map { it }.toTypedArray()).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false)
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(isLoading = false)
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(
                            message = result.message ?: Resources.getSystem()
                                .getString(R.string.could_not_save_note)
                        )
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteNotes(idList: List<Int>) {
        noteUseCases.deleteNotesUseCase(idList).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _eventFlow.emit(
                        UiEvent.ShowSnackbarAction(
                            message = "Note deleted",//Resources.getSystem().getString(R.string.note_deleted),
                            actionText = "Undo",//Resources.getSystem().getString(R.string.undo),
                            onClickAction = { onEvent(NoteListEvent.RestoreNotes) }
                        )
                    )
                }
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(
                            message = result.message ?: Resources.getSystem()
                                .getString(R.string.could_not_delete_note)
                        )
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun getDrawings() {
        for (note in _noteList) {
            if (note.drawName.isNotEmpty()) {
                val storageImage =
                    loadImageFromInternalStorage(note.drawName, application.applicationContext)
                if (storageImage.name.isNotEmpty()) {
                    note.drawBitMap = storageImage.bitmap
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data class ShowSnackbarAction(
            val message: String,
            val actionText: String,
            val onClickAction: () -> Unit
        ) : UiEvent()
    }
}