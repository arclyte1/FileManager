package com.example.filemanager.presentation.screen.file_browser

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.R
import com.example.filemanager.domain.model.BaseElement
import com.example.filemanager.domain.usecase.GetElementsUseCase
import com.example.filemanager.presentation.shared.element_details.BaseElementDetails
import com.example.filemanager.presentation.shared.element_details.ElementDetailsFormatter
import com.example.filemanager.presentation.shared.element_list_item.BaseListElement
import com.example.filemanager.presentation.shared.element_list_item.ListElementFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FileBrowserViewModel @Inject constructor(
    private val getElementsUseCase: GetElementsUseCase
) : ViewModel() {

    private val listElementFormatter = ListElementFormatter()
    private val detailsElementFormatter = ElementDetailsFormatter()

    private val updateScrollPositionEventsBackStack = LinkedList<Event.UpdateScrollPosition>()

    private val basePath = Environment.getExternalStorageDirectory().path
    private val _path = MutableStateFlow("")
    val path: StateFlow<String> = _path

    private val _listElements = MutableStateFlow<List<BaseElement>>(emptyList())
    private val _formattedListElements = MutableStateFlow<List<BaseListElement>>(emptyList())
    val formattedListElements: StateFlow<List<BaseListElement>> = _formattedListElements

    private val _sortBy = MutableStateFlow(SortBy.NAME)
    val sortBy: StateFlow<SortBy> = _sortBy

    private val _sortingOrder = MutableStateFlow(SortingOrder.ASC)
    val sortingOrder: StateFlow<SortingOrder> = _sortingOrder

    private val _elementDetails = MutableStateFlow<BaseElementDetails?>(null)
    val elementDetails: StateFlow<BaseElementDetails?> = _elementDetails

    val event = MutableStateFlow<Event>(Event.Clean)

    init {
        updateElementsList()
    }

    fun navigateUp() {
        _path.value = _path.value.substringBeforeLast("/")
        if (updateScrollPositionEventsBackStack.isNotEmpty()) {
            val event = updateScrollPositionEventsBackStack.removeLast()
            updateElementsList(event)
        } else {
            updateElementsList()
        }
    }

    fun navigateDirectory(dirName: String, itemPos: Int, offset: Int) {
        _path.value += "/$dirName"
        updateScrollPositionEventsBackStack.add(
            Event.UpdateScrollPosition(itemPos, offset)
        )
        updateElementsList()
    }

    fun setSortBy(sortBy: SortBy) {
        _sortBy.value = sortBy
        updateScrollPositionEventsBackStack.clear()
        updateElementsList()
    }

    fun setSortingOrder(order: SortingOrder) {
        _sortingOrder.value = order
        updateScrollPositionEventsBackStack.clear()
        updateElementsList()
    }

    fun setElementDetails(name: String) {
        _listElements.value.find { it.name == name }?.let { element ->
            _elementDetails.value = detailsElementFormatter.format(element, basePath)
        }
    }

    private fun updateElementsList(
        updateScrollPositionEvent: Event.UpdateScrollPosition = Event.UpdateScrollPosition(0, 0)
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            _listElements.value = getElementsUseCase(basePath + _path.value)
            sortElementList()
            _formattedListElements.value = _listElements.value.map { listElementFormatter.format(it) }
            event.value = updateScrollPositionEvent
        }
    }

    private fun sortElementList() {
        val sortingPredicate = { element: BaseElement ->
            when (_sortBy.value) {
                SortBy.NAME -> element.name
                SortBy.DATE -> element.dateModified.time.toString()
                SortBy.SIZE -> {
                    when (element) {
                        is BaseElement.FileElement -> element.size.toString()
                        is BaseElement.DirectoryElement -> element.elementsCount.toString()
                    }
                }
                SortBy.EXTENSION -> {
                    when (element) {
                        is BaseElement.FileElement -> element.extension
                        is BaseElement.DirectoryElement -> element.name
                    }
                }
            }
        }

        _listElements.value = if (_sortingOrder.value == SortingOrder.ASC) {
            _listElements.value.sortedBy(sortingPredicate)
        } else {
            _listElements.value.sortedByDescending(sortingPredicate)
        }
        _listElements.value = _listElements.value.sortedBy { it is BaseElement.FileElement }
    }

    sealed class Event {
        object Clean : Event()
        class UpdateScrollPosition(val itemPos: Int, val offset: Int) : Event()
    }


    enum class SortingOrder {
        ASC, DESC;
        operator fun unaryMinus(): SortingOrder {
            return when (this) {
                ASC -> DESC
                DESC -> ASC
            }
        }
    }
    enum class SortBy(val titleResourceId: Int) {
        NAME(R.string.name),
        SIZE(R.string.size),
        DATE(R.string.date),
        EXTENSION(R.string.extension);
    }
}