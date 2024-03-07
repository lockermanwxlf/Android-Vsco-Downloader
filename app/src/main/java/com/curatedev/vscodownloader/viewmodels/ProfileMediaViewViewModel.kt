package com.curatedev.vscodownloader.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.curatedev.vscodownloader.cache.CoilCacheDownloader
import com.curatedev.vscodownloader.vsco.VscoHandler
import com.curatedev.vscodownloader.vsco.dataclasses.VscoMedia
import com.curatedev.vscodownloader.vsco.dataclasses.VscoProfile
import com.curatedev.vscodownloader.worker.DownloadWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ProfileMediaViewViewModel @Inject constructor(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
    private val coilCacheDownloader: CoilCacheDownloader
): AndroidViewModel(application) {
    private val context = application.applicationContext
    val media = savedStateHandle.getStateFlow<List<VscoMedia>>("media", listOf())
    fun fetchMedia(vscoProfile: VscoProfile) = viewModelScope.launch(Dispatchers.IO) {
        savedStateHandle["media"] = VscoHandler.getMedia(vscoProfile.siteId)
    }

    fun download(vscoProfile: VscoProfile, vscoMedia: List<VscoMedia>) = viewModelScope.launch(Dispatchers.IO) {

        val request = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(Data.Builder()
                .putString("name", vscoProfile.name)
                .putLong("siteId", vscoProfile.siteId)
                .putString("uri", vscoProfile.imageUri.toString())
                .putString("imageId", vscoProfile.imageId)
                .build()
            )
            .build()
        WorkManager.getInstance(context)
            .enqueue(request)
    }

}