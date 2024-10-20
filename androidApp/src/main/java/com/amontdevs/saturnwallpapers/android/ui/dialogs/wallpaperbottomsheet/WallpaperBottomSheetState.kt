package com.amontdevs.saturnwallpapers.android.ui.dialogs.wallpaperbottomsheet

import com.amontdevs.saturnwallpapers.model.SaturnPhoto
import com.amontdevs.saturnwallpapers.model.SaturnPhotoWithMedia

data class WallpaperBottomSheetState(
    val saturnPhoto: SaturnPhotoWithMedia = SaturnPhotoWithMedia(
        SaturnPhoto(),
        listOf()
    ),
    val isSetWallpaperLockScreenLoading: Boolean = false,
    val isSetWallpaperHomeScreenLoading: Boolean = false,
    val isSetWallpaperBothLoading: Boolean = false,
    val isDownloadNormalLoading: Boolean = false,
    val isDownloadHQLoading: Boolean = false,
    val displayToast: Boolean = false,
    val toastMessage: String = ""
)
