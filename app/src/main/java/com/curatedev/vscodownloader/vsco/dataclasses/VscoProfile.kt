package com.curatedev.vscodownloader.vsco.dataclasses

import android.net.Uri
import android.os.Parcelable
import androidx.media3.common.MimeTypes
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class VscoProfile(
    val name: String,
    val siteId: Long,
    val imageId: String?,
    val imageUri: Uri
): Parcelable {
    @IgnoredOnParcel
    val pfpAsMedia = imageId?.let {
        VscoMedia(
            it,
            MimeTypes.IMAGE_JPEG,
            imageUri
        )
    }
}