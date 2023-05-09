package com.example.filemanager.presentation.screen.recent

import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.common.Resource
import com.example.filemanager.domain.model.BaseElement
import com.example.filemanager.domain.usecase.GetUpdatedOrNewFilesUseCase
import com.example.filemanager.presentation.shared.element_details.BaseElementDetails
import com.example.filemanager.presentation.shared.element_details.ElementDetailsFormatter
import com.example.filemanager.presentation.shared.element_list_item.BaseListElement
import com.example.filemanager.presentation.shared.element_list_item.ListElementFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RecentViewModel @Inject constructor(
    private val getUpdatedOrNewFilesUseCase: GetUpdatedOrNewFilesUseCase
) : ViewModel() {

    private val listElementFormatter = ListElementFormatter()
    private val detailsElementFormatter = ElementDetailsFormatter()

    private val basePath = Environment.getExternalStorageDirectory().path

    private val _filesList = MutableStateFlow<List<BaseElement.FileElement>>(emptyList())
    private val _formattedFilesList = MutableStateFlow<List<BaseListElement.FileListElement>>(emptyList())
    val formattedFilesList: StateFlow<List<BaseListElement.FileListElement>> = _formattedFilesList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _elementDetails = MutableStateFlow<BaseElementDetails?>(null)
    val elementDetails: StateFlow<BaseElementDetails?> = _elementDetails


    init {
        getUpdatedOrNewFilesUseCase().onEach { resource ->
            when(resource) {
                is Resource.Success -> {
                    _isLoading.emit(false)
                    _filesList.emit(resource.data!!)
                    updateList()
                }
                is Resource.Error -> {
                    _isLoading.emit(false)
                    _formattedFilesList.emit(emptyList())
                }
                is Resource.Loading -> {
                    _isLoading.emit(true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun updateList() {
        _filesList.value = _filesList.value.sortedBy { it.dateModified }
        _formattedFilesList.value = _filesList.value.map {
            listElementFormatter.format(it) as BaseListElement.FileListElement
        }
    }

    fun setElementDetails(name: String) {
        _filesList.value.find { it.name == name }?.let { element ->
            _elementDetails.value = detailsElementFormatter.format(element, basePath)
        }
    }
}