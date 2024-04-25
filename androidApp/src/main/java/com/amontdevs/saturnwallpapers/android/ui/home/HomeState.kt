package com.amontdevs.saturnwallpapers.android.ui.home

import com.amontdevs.saturnwallpapers.model.SaturnPhoto

data class HomeState(
    val saturnPhoto: SaturnPhoto? = null,
    val favoritePhotos: List<SaturnPhoto> = listOf()
)