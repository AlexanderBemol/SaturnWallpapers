package com.amontdevs.saturnwallpapers.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SaturnPhotoMedia(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var saturnPhotoId: Long,
    var mediaType: SaturnPhotoMediaType,
    var url: String,
    var filepath: String,
    var status: SaturnPhotoMediaStatus,
    var errorMessage: String,
)

enum class SaturnPhotoMediaStatus{
    NOT_DOWNLOADED_YET,
    DOWNLOADED,
    DELETED,
    ERROR
}

enum class SaturnPhotoMediaType{
    REGULAR_QUALITY_IMAGE,
    HIGH_QUALITY_IMAGE,
    VIDEO
}