package com.example.vscodownloader.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vscodownloader.vsco.VscoHandler
import com.example.vscodownloader.vsco.dataclasses.VscoMedia
import com.example.vscodownloader.vsco.dataclasses.VscoProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Path
import javax.inject.Inject
@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ProfileMediaViewViewModel @Inject constructor(
    private val application: Application,
    private val savedStateHandle: SavedStateHandle,
): AndroidViewModel(application) {
    private val context = application.applicationContext
    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    val media = savedStateHandle.getStateFlow<List<VscoMedia>>("media", listOf())
    fun fetchMedia(vscoProfile: VscoProfile) = viewModelScope.launch(Dispatchers.IO) {
        savedStateHandle["media"] = VscoHandler.getMedia(vscoProfile.siteId)
    }

    fun download(vscoProfile: VscoProfile, vscoMedia: List<VscoMedia>) {
        val mutableVscoMedia = vscoMedia.toMutableList()
        val contentUri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL)
        context.contentResolver.query(
            contentUri,
            arrayOf(MediaStore.Downloads.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            val index = cursor.getColumnIndex(MediaStore.Downloads.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                mutableVscoMedia.removeIf { it.filename == cursor.getString(index) }
            }
        }
        mutableVscoMedia.toList().forEach { currentVscoMedia ->
            val contentValues = ContentValues().apply {
                put(MediaStore.DownloadColumns.DISPLAY_NAME, currentVscoMedia.filename)
                put(MediaStore.DownloadColumns.MIME_TYPE, currentVscoMedia.mime)
                put(MediaStore.DownloadColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + vscoProfile.name)
            }
            context.contentResolver.insert(contentUri, contentValues)?.let { fileUri ->
                val request = DownloadManager.Request(currentVscoMedia.downloadUri)
                VscoHandler.headers.forEach {
                    request.addRequestHeader(it.first, it.second)
                }
                //request.setDestinationUri(fileUri)
                val query = downloadManager.enqueue(request)
            }
        }
    }

}