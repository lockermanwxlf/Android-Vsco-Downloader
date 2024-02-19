package com.example.vscodownloader.vsco.dataclasses

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VscoMedia(
    val id: String,
    val mime: String,
    val downloadUri: Uri
): Parcelable