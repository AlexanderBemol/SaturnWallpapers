package com.amontdevs.saturnwallpapers.android.ui.photodetail

import com.amontdevs.saturnwallpapers.model.SaturnPhotoWithMedia

data class PhotoDetailState(
    val saturnPhoto: SaturnPhotoWithMedia? = null,
    val isHighQuality: Boolean = false
)
