package com.amontdevs.saturnwallpapers.android.ui.gallery

import com.amontdevs.saturnwallpapers.model.SaturnPhotoWithMedia

data class GalleryState(
    val isFavoriteSelected: Boolean  = false,
    val isAscSortSelected: Boolean = false,
    val areFiltersVisible: Boolean = false,
    val saturnPhotos: List<SaturnPhotoWithMedia> = listOf(),
    val isFetchingPhotos: Boolean = false,
    val isLoaded: Boolean = false,
    val pendingPhotosToDownload: Int = 4
)