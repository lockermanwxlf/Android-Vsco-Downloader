package com.curatedev.vscodownloader.vsco.gson

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import com.curatedev.vscodownloader.vsco.dataclasses.VscoMedia
import com.curatedev.vscodownloader.vsco.dataclasses.VscoProfile
import com.google.gson.annotations.SerializedName

@OptIn(UnstableApi::class)
class VscoGson {
    data class GridsResult(
        val results: List<GridsProfile>
    )
    data class GridsProfile(
        val gridImageId: String?,
        @SerializedName("responsive_url") val responsiveUrl: String,
        val siteId: Long,
        val siteSubDomain: String
    ) {
        fun toVscoProfile() = VscoProfile(
            this.siteSubDomain,
            this.siteId,
            this.gridImageId,
            Uri.parse(this.responsiveUrl).buildUpon().scheme("https").build()
        )
    }

    data class MediaResult(
        val media: List<Media>,
        @SerializedName("previous_cursor") val previousCursor: String?,
        @SerializedName("next_cursor") val nextCursor: String?
    )

    data class Image(
        @SerializedName("_id") val id: String,
        @SerializedName("upload_date") val uploadDate: Long,
        @SerializedName("responsive_url") val responsiveUrl: String
    ) {
        fun toVscoMedia() = VscoMedia(
            this.id,
            MimeTypes.IMAGE_PNG,
            Uri.parse(this.responsiveUrl).buildUpon().scheme("https").build()
        )
    }
    data class Video(
        @SerializedName("_id") val id: String,
        @SerializedName("upload_date") val uploadDate: Long,
        @SerializedName("video_url") val videoUrl: String
    ) {
        fun toVscoMedia() = VscoMedia(
            this.id,
            MimeTypes.VIDEO_H265,
            Uri.parse(this.videoUrl).buildUpon().scheme("https").build()
        )
    }

    data class Media(
        val type: String,
        val image: Image?,
        val video: Video?
    ) {
        fun toVscoMedia() = image?.toVscoMedia()?:video!!.toVscoMedia()
    }
}