package com.amontdevs.saturnwallpapers.android.ui.dialogs.wallpaperbottomsheet

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amontdevs.saturnwallpapers.android.system.AndroidWallpaperSetter
import com.amontdevs.saturnwallpapers.android.system.IAndroidWallpaperSetter
import com.amontdevs.saturnwallpapers.model.MediaQuality
import com.amontdevs.saturnwallpapers.model.SaturnPhotoMediaType
import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.model.WallpaperScreen
import com.amontdevs.saturnwallpapers.model.getMedia
import com.amontdevs.saturnwallpapers.repository.ISaturnPhotosRepository
import com.amontdevs.saturnwallpapers.repository.ISettingsRepository
import com.amontdevs.saturnwallpapers.repository.SaturnPhotosRepository
import com.amontdevs.saturnwallpapers.source.IFileManager
import com.amontdevs.saturnwallpapers.system.IWallpaperSetter
import com.amontdevs.saturnwallpapers.system.WallpaperSetter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WallpaperBottomSheetViewModel(
    private val saturnPhotosRepository: ISaturnPhotosRepository,
    private val fileManager: IFileManager,
    private val wallpaperSetter: IWallpaperSetter,
    private val androidWallpaperSetter: IAndroidWallpaperSetter,
    private val photoId: Long
) : ViewModel() {
    private val _wallpaperBottomSheetState = MutableStateFlow(WallpaperBottomSheetState())
    val wallpaperBottomSheetState: StateFlow<WallpaperBottomSheetState> = _wallpaperBottomSheetState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            when (val result = saturnPhotosRepository.getSaturnPhoto(photoId)) {
                is SaturnResult.Success -> {
                    _wallpaperBottomSheetState.value = _wallpaperBottomSheetState.value.copy(
                        saturnPhoto = result.data,
                        isSetWallpaperHomeScreenLoading = false,
                        isSetWallpaperLockScreenLoading = false,
                        isSetWallpaperBothLoading = false,
                        isDownloadNormalLoading = false,
                        isDownloadHQLoading = false,
                        displayToast = false,
                        toastMessage = ""
                    )
                }
                is SaturnResult.Error -> {
                    Log.e("WallpaperBottomSheetViewModel", "loadData: ${result.e}")
                }
            }
        }
    }

    fun downloadPhoto(quality: MediaQuality) {
        _wallpaperBottomSheetState.value = _wallpaperBottomSheetState.value.copy(
            isDownloadHQLoading = quality == MediaQuality.HIGH,
            isDownloadNormalLoading = quality == MediaQuality.NORMAL
        )
        val saturnPhoto = _wallpaperBottomSheetState.value.saturnPhoto
        viewModelScope.launch {
            val media = saturnPhoto.getMedia(
                if(quality == MediaQuality.HIGH) SaturnPhotoMediaType.HIGH_QUALITY_IMAGE
                else if (saturnPhoto.saturnPhoto.isVideo) SaturnPhotoMediaType.VIDEO
                else SaturnPhotoMediaType.REGULAR_QUALITY_IMAGE
            )

            when (val result = fileManager.savePictureToExternalStorage(media?.filepath.toString())) {
                is SaturnResult.Success -> {
                    _wallpaperBottomSheetState.value = _wallpaperBottomSheetState.value.copy(
                        isDownloadHQLoading = false,
                        isDownloadNormalLoading = false,
                        displayToast = true,
                        toastMessage = "Photo downloaded"
                    )
                }
                is SaturnResult.Error -> {
                    Log.e("WallpaperBottomSheetViewModel", "downloadPhoto: ${result.e}")
                    _wallpaperBottomSheetState.value = _wallpaperBottomSheetState.value.copy(
                        isDownloadHQLoading = false,
                        isDownloadNormalLoading = false,
                        displayToast = true,
                        toastMessage = "Failed to download photo"
                    )
                }
            }
        }
    }

    fun setWallpaper(screen: WallpaperScreen) {
        val platformSetWallpaper = { wallpaperScreen: WallpaperScreen, byteArray: ByteArray ->
            androidWallpaperSetter.setWallpaper(wallpaperScreen, byteArray)
        }
        viewModelScope.launch {

            _wallpaperBottomSheetState.value = when (screen) {
                WallpaperScreen.HOME_SCREEN -> wallpaperBottomSheetState.value.copy(isSetWallpaperHomeScreenLoading = true)
                WallpaperScreen.LOCK_SCREEN -> wallpaperBottomSheetState.value.copy(isSetWallpaperLockScreenLoading = true)
                WallpaperScreen.ALL -> wallpaperBottomSheetState.value.copy(isSetWallpaperBothLoading = true)
            }

            val saturnPhoto = _wallpaperBottomSheetState.value.saturnPhoto
            val picturePath = (saturnPhoto.getMedia(SaturnPhotoMediaType.HIGH_QUALITY_IMAGE)?.filepath
                ?: saturnPhoto.getMedia(SaturnPhotoMediaType.REGULAR_QUALITY_IMAGE)?.filepath
                ?: saturnPhoto.getMedia(SaturnPhotoMediaType.VIDEO)?.filepath).toString()

            when (val result = wallpaperSetter.setWallpaper(screen, picturePath, platformSetWallpaper)){
                is SaturnResult.Success -> {
                    _wallpaperBottomSheetState.value = _wallpaperBottomSheetState.value.copy(
                        isSetWallpaperHomeScreenLoading = false,
                        isSetWallpaperLockScreenLoading = false,
                        isSetWallpaperBothLoading = false,
                        displayToast = true,
                        toastMessage = "Wallpaper set"
                    )
                }
                is SaturnResult.Error -> {
                    Log.e("WallpaperBottomSheetViewModel", "setWallpaper: ${result.e}")
                    _wallpaperBottomSheetState.value = _wallpaperBottomSheetState.value.copy(
                        isSetWallpaperHomeScreenLoading = false,
                        isSetWallpaperLockScreenLoading = false,
                        isSetWallpaperBothLoading = false,
                        displayToast = true,
                        toastMessage = "Failed to set wallpaper"
                    )
                }
            }
        }
    }

}