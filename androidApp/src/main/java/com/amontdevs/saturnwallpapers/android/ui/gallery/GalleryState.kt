package com.amontdevs.saturnwallpapers.android.ui.gallery

import com.amontdevs.saturnwallpapers.model.SaturnPhoto

data class GalleryState(
    val isFavoriteSelected: Boolean  = false,
    val isAscSortSelected: Boolean = false,
    val areFiltersVisible: Boolean = false,
    val saturnPhotos: List<SaturnPhoto> = listOf(),
    val isFetchingPhotos: Boolean = false,
    val isLoaded: Boolean = false
)