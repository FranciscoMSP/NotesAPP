package com.fmspcoding.notesapp.presentation.note_detail

import android.provider.ContactsContract.CommonDataKinds.Note

sealed class NoteDetailEvent {
    data class EnteredTitle(val value: String): NoteDetailEvent()
    data class EnteredDescription(val value: String): NoteDetailEvent()
    data class EnteredItemListText(val value: String, val index: Int): NoteDetailEvent()
    data class CheckedItem(val value: Boolean, val index: Int): NoteDetailEvent()
    data class DeleteItem(val index: Int): NoteDetailEvent()
    data class DetailItemClick(val noteMode: NoteDetailMode): NoteDetailEvent()
    object OpenDetailMenu: NoteDetailEvent()
    object AddCheckList: NoteDetailEvent()
    object AddItemToList: NoteDetailEvent()
    object DeleteNote: NoteDetailEvent()
    object SaveNote: NoteDetailEvent()
    object DialogDismiss: NoteDetailEvent()
    object DialogConfirm: NoteDetailEvent()
}