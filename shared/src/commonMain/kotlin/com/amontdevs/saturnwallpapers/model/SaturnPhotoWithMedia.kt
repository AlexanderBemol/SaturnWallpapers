package com.amontdevs.saturnwallpapers.model

import androidx.room.Embedded
import androidx.room.Relation

data class SaturnPhotoWithMedia(
    @Embedded var saturnPhoto: SaturnPhoto,
    @Relation(
        parentColumn = "id",
        entityColumn = "saturnPhotoId"
    )
    val mediaList: List<SaturnPhotoMedia>
)

fun SaturnPhotoWithMedia.getMedia(mediaType: SaturnPhotoMediaType) = when (mediaType) {
    SaturnPhotoMediaType.REGULAR_QUALITY_IMAGE ->
        mediaList.find { it.mediaType == SaturnPhotoMediaType.REGULAR_QUALITY_IMAGE
                && it.filepath != ""
                && it.status == SaturnPhotoMediaStatus.DOWNLOADED  }
    SaturnPhotoMediaType.VIDEO ->
        mediaList.find { it.mediaType == SaturnPhotoMediaType.VIDEO
                && it.filepath != ""
                && it.status == SaturnPhotoMediaStatus.DOWNLOADED }
    SaturnPhotoMediaType.HIGH_QUALITY_IMAGE ->
            mediaList.find { it.mediaType == SaturnPhotoMediaType.HIGH_QUALITY_IMAGE
                    && it.filepath != ""
                    && it.status == SaturnPhotoMediaStatus.DOWNLOADED
            }
}
