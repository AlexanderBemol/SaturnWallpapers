package com.amontdevs.saturnwallpapers.android.ui.home

import com.amontdevs.saturnwallpapers.model.SaturnPhotoWithMedia

data class HomeState(
    val saturnPhoto: SaturnPhotoWithMedia? = null,
    val favoritePhotos: List<SaturnPhotoWithMedia> = listOf()
)