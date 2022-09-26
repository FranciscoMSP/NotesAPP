package com.fmspcoding.notesapp.presentation.note_list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmspcoding.notesapp.core.util.Resource
import com.fmspcoding.notesapp.domain.use_case.GetNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase
): ViewModel() {

    private val _state = mutableStateOf(NoteListState())
    val state: State<NoteListState> = _state

    init {
        getNotes()
    }

    private fun getNotes() {
        getNotesUseCase().onEach { result ->
            when(result) {
                is Resource.Loading -> {
                    _state.value = NoteListState(isLoading = true)
                }
                is Resource.Success ->  _state.value = NoteListState(notes = result.data ?: emptyList())
                is Resource.Error -> {
                    _state.value = NoteListState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}