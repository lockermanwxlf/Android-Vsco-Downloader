package com.example.vscodownloader.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vscodownloader.vsco.VscoHandler
import com.example.vscodownloader.vsco.dataclasses.VscoMedia
import com.example.vscodownloader.vsco.dataclasses.VscoProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ProfileMediaViewViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    val media = savedStateHandle.getStateFlow<List<VscoMedia>>("media", listOf())
    fun fetchMedia(vscoProfile: VscoProfile) = viewModelScope.launch(Dispatchers.IO) {
        savedStateHandle["media"] = VscoHandler.getMedia(vscoProfile.siteId)
    }
}