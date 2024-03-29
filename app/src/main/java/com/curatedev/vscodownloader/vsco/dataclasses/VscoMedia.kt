package com.curatedev.vscodownloader.vsco.dataclasses

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class VscoMedia(
    val id: String,
    val mime: String,
    val downloadUri: Uri
): Parcelable {
    @IgnoredOnParcel
    val filename = id
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as VscoMedia

        return id == other.id
    }
}