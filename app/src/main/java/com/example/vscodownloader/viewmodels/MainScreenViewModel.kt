package com.example.vscodownloader.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vscodownloader.vsco.VscoHandler
import com.example.vscodownloader.vsco.dataclasses.VscoProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel()
class MainScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    val searchInput = savedStateHandle.getStateFlow("searchInput", "")
    val searchResult = savedStateHandle.getStateFlow<List<VscoProfile>>("result", listOf())
    val selectedProfile = savedStateHandle.getStateFlow<VscoProfile?>("selectedProfile", null)

    fun setSearchInput(s: String) {
        savedStateHandle["searchInput"] = s
    }

    fun search() = viewModelScope.launch(Dispatchers.IO) {
        savedStateHandle["selectedProfile"] = null
        savedStateHandle["result"] = VscoHandler.search(searchInput.value)
    }

    fun selectProfile(p: VscoProfile) {
        savedStateHandle["selectedProfile"] = p
    }
}