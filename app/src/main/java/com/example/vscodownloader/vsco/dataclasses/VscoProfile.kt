package com.example.vscodownloader.vsco.dataclasses

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class VscoProfile(
    val name: String,
    val siteId: Long,
    val imageId: String?,
    val imageUri: Uri?
): Parcelable