package com.fmspcoding.notesapp.presentation.note_detail

import android.provider.SyncStateContract
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmspcoding.notesapp.core.Constants
import com.fmspcoding.notesapp.core.util.Resource
import com.fmspcoding.notesapp.domain.model.Note
import com.fmspcoding.notesapp.domain.use_case.GetNoteUseCase
import com.fmspcoding.notesapp.domain.use_case.GetNotesUseCase
import com.fmspcoding.notesapp.domain.use_case.InsertNoteUseCase
import com.fmspcoding.notesapp.domain.use_case.UpdateNoteUseCase
import com.fmspcoding.notesapp.presentation.note_list.NoteListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val getNoteUseCase: GetNoteUseCase,
    private val insertNoteUseCase: InsertNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _title = mutableStateOf("")
    val title: State<String> = _title

    private val _description = mutableStateOf("")
    val description: State<String> = _description

    private val _state = mutableStateOf(NoteDetailState())
    val state: State<NoteDetailState> = _state

    init {
        savedStateHandle.get<Int>(Constants.PARAM_NOTE_ID)?.let { noteId ->
            if(noteId > 0) {
                getNote(noteId)
            }
        }
    }

    fun onTitle(value: String) {
        _title.value = value
    }

    fun onDescription(value: String) {
        _description.value = value
    }

    private fun getNote(noteId: Int) {
        getNoteUseCase(noteId).onEach { result ->
            when(result) {
                is Resource.Loading -> {
                    _state.value = NoteDetailState(isLoading = true)
                }
                is Resource.Success ->  {
                    _state.value = NoteDetailState(note = result.data ?: Note())
                    _title.value = _state.value.note!!.title
                    _description.value = _state.value.note!!.description
                }
                is Resource.Error -> {
                    _state.value = NoteDetailState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun insertNote() {
        val note = Note(id = 0, title = title.value, description = description.value)
        println("Aqui")
        insertNoteUseCase(note).onEach { result ->
            when(result) {
                is Resource.Loading -> {
                    _state.value = NoteDetailState(isLoading = true)
                }
                is Resource.Success ->  {
                    println("Aqui2")
                    _state.value = NoteDetailState(isLoading = false)
                }
                is Resource.Error -> {
                    _state.value = NoteDetailState(
                        error = result.message ?: "An unexpected error occured",
                        isLoading = false
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}