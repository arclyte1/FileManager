package com.example.filemanager.presentation.screen.file_browser

import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.util.Log
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
    private val getElementsUseCase: GetElementsUseCase,
    private val storageManager: StorageManager
) : ViewModel() {

    private val listElementFormatter = ListElementFormatter()
    private val detailsElementFormatter = ElementDetailsFormatter()

    private val updateScrollPositionEventsBackStack = LinkedList<Event.UpdateScrollPosition>()

    private val _volumesList = MutableStateFlow<List<StorageVolume>>(storageManager.storageVolumes)
    private val _formattedVolumesList = MutableStateFlow<List<StorageVolumeListItem>>(
        _volumesList.value.map {
            StorageVolumeListItem(
                title = if (it.uuid == null) null else it.toString().removePrefix("StorageVolume: "),
                uuid = it.uuid
            )
        }
    )
    val formattedVolumesList: StateFlow<List<StorageVolumeListItem>> = _formattedVolumesList

    private val _selectedVolume = MutableStateFlow(
        _formattedVolumesList.value.find { it.uuid == null } ?: _formattedVolumesList.value[0]
    )
    val selectedVolume: StateFlow<StorageVolumeListItem> = _selectedVolume

    private lateinit var basePath: String
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        selectVolume(null)
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

    fun selectVolume(uuid: String?) {
        basePath = if (uuid == null) "/storage/emulated/0" else "/storage/$uuid"
        _selectedVolume.value = _formattedVolumesList.value.find { it.uuid == uuid } ?: _formattedVolumesList.value[0]
        Log.d("ViewModel", _selectedVolume.value.title.toString())
        updateScrollPositionEventsBackStack.clear()
        updateElementsList()
    }

    private fun updateElementsList(
        updateScrollPositionEvent: Event.UpdateScrollPosition = Event.UpdateScrollPosition(0, 0)
    ) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.Default) {
            _listElements.value = getElementsUseCase(basePath + _path.value)
            sortElementList()
            _formattedListElements.value = _listElements.value.map { listElementFormatter.format(it) }
            event.value = updateScrollPositionEvent
            _isLoading.value = false
        }
    }

    private fun sortElementList() {
        _listElements.value = when(sortBy.value) {
            SortBy.NAME -> {
                _listElements.value.sortedBy { it.name }
            }
            SortBy.SIZE -> {
                _listElements.value.sortedBy {
                    when (it) {
                        is BaseElement.FileElement -> it.size
                        is BaseElement.DirectoryElement -> it.elementsCount.toLong()
                    }
                }
            }
            SortBy.DATE -> {
                _listElements.value.sortedBy { it.dateModified }
            }
            SortBy.EXTENSION -> {
                _listElements.value.sortedBy {
                    when (it) {
                        is BaseElement.FileElement -> it.extension
                        is BaseElement.DirectoryElement -> it.name
                    }
                }
            }
        }

        if (_sortingOrder.value == SortingOrder.DESC)
            _listElements.value = _listElements.value.asReversed()

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