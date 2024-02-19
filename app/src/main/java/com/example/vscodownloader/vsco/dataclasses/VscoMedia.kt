package com.example.vscodownloader.vsco.dataclasses

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Parcelable
import com.example.vscodownloader.vsco.VscoHandler
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class VscoMedia(
    val id: String,
    val mime: String,
    val downloadUri: Uri
): Parcelable {
    suspend fun download(context: Context) {
        val request = DownloadManager.Request(this.downloadUri)
        VscoHandler.headers.forEach {
            request.addRequestHeader(it.first, it.second)
        }

    }

    @IgnoredOnParcel
    val filename = id
}