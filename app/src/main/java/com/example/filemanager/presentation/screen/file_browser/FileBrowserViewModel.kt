package com.example.filemanager.presentation.screen.file_browser

import android.os.Environment
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.filemanager.domain.model.BaseElement
import com.example.filemanager.domain.usecase.GetElementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FileBrowserViewModel @Inject constructor(
    private val getElementsUseCase: GetElementsUseCase
) : ViewModel() {

    private val elementFormatter = ListElementFormatter()
    private val updateScrollPositionEventsBackStack = LinkedList<Event.UpdateScrollPosition>()
    private val basePath = Environment.getExternalStorageDirectory().path
    val path = mutableStateOf("")
    val listElements = mutableStateOf<List<BaseListElement>>(emptyList())
    val event = mutableStateOf<Event>(Event.Clean)

    init {
        updateElementsList()
    }

    fun navigateUp() {
        path.value = path.value.substringBeforeLast("/")
        val event = updateScrollPositionEventsBackStack.removeLast()
        if (event != null) {
            updateElementsList(event)
        } else {
            updateElementsList()
        }
    }

    fun navigateDirectory(dirName: String, itemPos: Int, offset: Int) {
        path.value += "/$dirName"
        updateScrollPositionEventsBackStack.add(
            Event.UpdateScrollPosition(itemPos, offset)
        )
        updateElementsList()
    }

    private fun updateElementsList(
        updateScrollPositionEvent: Event.UpdateScrollPosition = Event.UpdateScrollPosition(0, 0)
    ) {
        listElements.value = getElementsUseCase(basePath + path.value)
            .sortedBy { it.name }
            .sortedBy { it is BaseElement.FileElement }
            .map { elementFormatter.format(it) }
        event.value = updateScrollPositionEvent
    }

    sealed class Event {
        object Clean : Event()
        class UpdateScrollPosition(val itemPos: Int, val offset: Int) : Event()
    }
}